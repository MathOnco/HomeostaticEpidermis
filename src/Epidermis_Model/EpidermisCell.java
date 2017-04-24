package Epidermis_Model;

import AgentFramework.AgentSQ2;
import cern.jet.random.Poisson;
import cern.jet.random.engine.DRand;
import cern.jet.random.engine.RandomEngine;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import static AgentFramework.Utils.ModWrap;
import static Epidermis_Model.EpidermisCellGenome.ExpectedMuts;
import static Epidermis_Model.EpidermisCellGenome.GeneLengths;
import static Epidermis_Model.EpidermisCellGenome.RN;
import static Epidermis_Model.EpidermisConst.*;

/**
 * Created by schencro on 3/31/17.
 */


class EpidermisCell extends AgentSQ2<EpidermisGrid> {
    /**
     * parameters that may be changed for cell behavior
     **/
    double prolif_scale_factor = 0.07610124; //Correction for appropriate proliferation rate (Default = 0.15-0.2 with KERATINO_APOPTOSIS_EGF=0.01)
    double KERATINO_EGF_CONSPUMPTION = -0.002269758; //consumption rate by keratinocytes
    double KERATINO_APOPTOSIS_EGF = 0.3358162; //level at which apoptosis occurs by chance (above this and no apoptosis)
    double DEATH_PROB = 0.01049936; //Overall Death Probability
    double MOVEPROBABILITY = 0.3657964; //RN float has to be greater than this to move...
    double DIVISIONLOCPROB = 0.8315265; // Probability of dividing up vs side to side
    static int pro_count = 0;
    static int pro_count_basal = 0;
    static int loss_count_basal = 0;
    static int death_count = 0;
    int myType; //cell type
    int Action; //cells action
    static public RandomEngine RNEngine = new DRand();
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
            if(G().ItoAgent(inBounds[i]) == null){
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
        if (myType == KERATINOCYTE && G().RN.nextDouble() > G().EGF.SQgetCurr(x, y) * prolif_scale_factor) {
            return false;
        }

//        if(y==0){
            int divOptions = GetEmptyVNSquares(x, y, false, G().divHoodBasal, G().inBounds); // Number of coordinates you could divide into
            iDivLoc = ProlifLoc(); // Where the new cell is going to be (which index) if basal cell
            if(iDivLoc==0 || iDivLoc==1 && y==0){
                loss_count_basal+=1;
            }
            boolean Pushed = CellPush(iDivLoc);
            if(Pushed==false){
                return false; // Only false if melanocyte there
            }

//        } else{
//            int divOptions = GetEmptyVNSquares(x, y, true, G().divHood, G().inBounds); // Number of coordinates you could divide into
//            if(divOptions>0){iDivLoc = G().RN.nextInt(divOptions);} else {return false;} //Where the new cell is going to be (which coordinate)
//
//
//        }

        EpidermisCell newCell = G().NewAgent(G().inBounds[iDivLoc]);

        if (y == 0) {
            pro_count_basal++;
        }

        newCell.init(myType, myGenome.NewClone().PossiblyMutate()); // initializes a new skin cell, pass the cellID for a new value each time.
        myGenome = myGenome.PossiblyMutate(); // Check if this daughter cell, i.e. the progenitor gets mutations during this proliferation step.
        pro_count += 1;
        return true;
    }

    public boolean CellPush(int iDivLoc){
        int i = G().inBounds[iDivLoc];
        EpidermisCell c=G().ItoAgent(i);
        if(c!=null){
            int x = G().ItoX(i);
            int y = G().ItoY(i);
            //look up for empty square
            int colTop=y;
//            EpidermisCell c=G().ItoAgent(i);
            while(c!=null){
                colTop++;
                c=G().SQtoAgent(x,colTop);
            }
            int colMax=colTop;
            //move column of cells up
            for(;colTop>y;colTop--){
                c=(G().SQtoAgent(x,colTop-1));
                c.Move(x,colTop);
            }
            if(c.Ysq()>= G().yDim-2){c.itDead();}
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
        death_count+=1;
        G().MeanDeath[Isq()] += 1;
        if (Ysq()==0){
            loss_count_basal++;
        }
    }

    public void CellStep(){
        int x=Xsq();int y=Ysq(); // Get discrete x and y coordinates
        Action = STATIONARY;
        if (y>=G().AIR_HEIGHT){
            itDead();
            return;
        }
        if (G().EGF.SQgetCurr(x, y) < KERATINO_APOPTOSIS_EGF && G().RN.nextDouble() < (Math.pow(1.0 - G().EGF.SQgetCurr(x, y) / KERATINO_APOPTOSIS_EGF, 5))) {
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
                Move(G().inBounds[iMoveCoord]); // We are moving
                Action = MOVING;
                if (Ysq() != 0 && y == 0) {
                    loss_count_basal++;
                }
            }
        }

        boolean divided = CheckProliferate();
        if(divided){
            Action = DIVIDE;
        }

    }

//    // Builds my genome information for data analysis
//    String ToString(){
//        String cellInfo="{["+createStrID()+"];[";
//        for(int iGene=0;iGene<myGenome.genomelength;iGene++){
//            cellInfo+="[";
//            for(int iMut=0;iMut<myGenome.mut_pos[iGene].size();iMut++){
//                cellInfo+=myGenome.mut_pos_getter(iGene,iMut)+",";
//            }
//            cellInfo+="],";
//        }
//        cellInfo+="]}";
//        return cellInfo;
//    }

}
