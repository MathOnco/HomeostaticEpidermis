package Epidermis_Model;

import Framework.Grids.AgentSQ2;
import cern.jet.random.engine.DRand;
import cern.jet.random.engine.RandomEngine;

import static Epidermis_Model.EpidermisCellGenome.RN;
import static Epidermis_Model.EpidermisConst.*;

/**
 * Created by schencro on 3/31/17.
 */


class EpidermisCell extends AgentSQ2<EpidermisGrid> {
    /**
     * parameters that may be changed for cell behavior
     **/
    public static  RandomEngine RNEngine = new DRand();
    double prolif_scale_factor = 0.07610124; //Correction for appropriate proliferation rate (Default = 0.15-0.2 with KERATINO_APOPTOSIS_EGF=0.01)
    double KERATINO_EGF_CONSPUMPTION = -0.002269758; //consumption rate by keratinocytes
    //    double KERATINO_EGF_CONSPUMPTION = 0.0;
    double KERATINO_APOPTOSIS_EGF = 0.3358162; //level at which apoptosis occurs by chance (above this and no apoptosis)
    double DEATH_PROB = 0.01049936; //Overall Death Probability
    double MOVEPROBABILITY = 0.3657964; //RN float has to be greater than this to move...
    double DIVISIONLOCPROB = 0.8315265; // Probability of dividing up vs side to side
    int myType; //cell type
    int Action; //cells action

    /**
     * Parameters for cell specific tracking and genome information
     **/
    // Clonal dynamic tracking
    EpidermisCellGenome myGenome; // Creating genome class within each cell

    public void init(int cellType, EpidermisCellGenome myGenome) { //This initilizes an agent with whatever is inside of this function...
        this.myType = cellType;
        this.Action = STATIONARY;
        // Storing Genome Reference to Parent and Itself if mutation happened
        this.myGenome = myGenome;
    }

    // Set coords array using this function
    // Function gets all cells surrounding it that are empty!! Take-Away
    public int GetEmptyVNSquares(int x, int y, boolean OnlyEmpty, int[] divHood, int[] inBounds){
        int finalCount=0;
        int inBoundsCount = G().SQsToLocalIs(divHood, inBounds, x, y, true, false); // Gets all inbound indices
        if(!OnlyEmpty){
            return inBoundsCount;
        }
        for (int i=0; i<inBoundsCount; i++){
            if(G().GetAgent(inBounds[i]) == null){
                inBounds[finalCount]=inBounds[i];
                finalCount++;
            }
        }
        return finalCount;
    }

    // Gets where a cell is dividing if it's a basal cell and is proliferating
    public int ProlifLoc(){
        double divideWhere = G().RN.nextDouble();
        double OtherOptionProb=(1-DIVISIONLOCPROB)/2.0;
        if(divideWhere<=DIVISIONLOCPROB){
            return 2; // Dividing up
        } else if(divideWhere>(DIVISIONLOCPROB+OtherOptionProb)){
            return 0; // Dividing right
        } else {
            return 1; // Dividing Left
        }
    }


    //Checking if a cell is going to proliferate...
    public boolean CheckProliferate() {
        int x = Xsq();
        int y = Ysq();
        int iDivLoc;

        // If EGF is low then next double is likely to be higher...Results in no proliferation
        if (myType == KERATINOCYTE && G().RN.nextDouble() > G().EGF.GetCurr(x, y) * prolif_scale_factor) {
            return false;
        }

        iDivLoc = ProlifLoc(); // Where the new cell is going to be (which index) if basal cell

        GetEmptyVNSquares(x,y,false,G().divHoodBasal, G().inBounds);

        boolean Pushed = CellPush(iDivLoc);
        if(Pushed!=false && y==0){
            G().Turnover.RecordLossBasal(); // Record Cell Loss from Pushing
        }

        EpidermisCell newCell = G().NewAgentI(G().inBounds[iDivLoc]);

        newCell.init(myType, myGenome.NewChild().PossiblyMutate()); // initializes a new skin cell, pass the cellID for a new value each time.

        myGenome = myGenome.PossiblyMutate(); // Check if this daughter cell, i.e. the progenitor gets mutations during this proliferation step.

        if(newCell.Ysq()==0){
            G().Turnover.RecordDivideBasal();
            G().Turnover.RecordDivideTissue();
        } else {
            G().Turnover.RecordDivideTissue();
        }

        return true;
    }

    public boolean CellPush(int iDivLoc){
        int i = G().inBounds[iDivLoc];
        EpidermisCell c=G().GetAgent(i);
        if(c!=null){
            int x = G().ItoX(i);
            int y = G().ItoY(i);

            //look up for empty square
            int colTop=y;
            while(c!=null){
                colTop++;
                c=G().GetAgent(x,colTop);
            }

            //move column of cells up
            for(;colTop>y;colTop--){
                c=(G().GetAgent(x,colTop-1));
                c.MoveSQ(x, colTop);
                if(c.Ysq()>= G().yDim-2){c.itDead();}
            }

            return true;
        } else{
            return false;
        }
    }

    // Sets the coordinates for a cell that is moving.
    public int GetMoveCoords() {
        int iMoveCoord=-1;  //when it's time to move, it is the index of coordinate that is picked from Coords array above. -1 == Not Moving
        int MoveOptions=GetEmptyVNSquares(Xsq(),Ysq(),true, G().moveHood, G().inBounds);
        if(MoveOptions>0&&myType==KERATINOCYTE) {
            iMoveCoord=G().RN.nextInt(MoveOptions);
        }
        return iMoveCoord;
    }

    public void itDead(){
        myGenome.DisposeClone(); // Decrements Population
        Dispose();

        G().MeanDeath[Isq()] += 1;
        if(Ysq()==0){
            G().Turnover.RecordLossBasal();
        }
        G().Turnover.RecordLossTissue();
    }

    public void CellStep(){
        int x=Xsq();int y=Ysq(); // Get discrete x and y coordinates
        Action = STATIONARY;
        if (y>=G().AIR_HEIGHT){
            itDead();
            return;
        }
        if (G().EGF.GetCurr(x, y) < KERATINO_APOPTOSIS_EGF && G().RN.nextDouble() < (Math.pow(1.0 - G().EGF.GetCurr(x, y) / KERATINO_APOPTOSIS_EGF, 5))) {
            //DEATH FROM LACK OF NUTRIENTS KERATINOCYTE
            itDead();
            return;
        }
        if(RN.nextDouble() < DEATH_PROB){
            //Random Fucked
            itDead();
            return;
        }

        if (G().RN.nextFloat() >= MOVEPROBABILITY) {
            int iMoveCoord = GetMoveCoords(); // -1 if not moving
            if (iMoveCoord != -1) {
                MoveI(G().inBounds[iMoveCoord]); // We are moving
                Action = MOVING;
                if (Ysq() > y) {
                    throw new RuntimeException("Cell is Moving Up.");
                }
            }
        }

        boolean divided = CheckProliferate();
        if(divided){
            Action = DIVIDE;
        }

    }

}