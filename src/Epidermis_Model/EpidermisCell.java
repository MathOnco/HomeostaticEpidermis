package Epidermis_Model;

import AgentFramework.AgentSQ2;
import cern.jet.random.Poisson;
import cern.jet.random.engine.DRand;
import cern.jet.random.engine.RandomEngine;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import static AgentFramework.Utils.ModWrap;
import static Epidermis_Model.EpidermisCellGenome.GeneMutations;
import static Epidermis_Model.EpidermisCellGenome.genelengths;
import static Epidermis_Model.EpidermisConst.*;

/**
 * Created by schencro on 3/31/17.
 */


class EpidermisCell extends AgentSQ2<EpidermisGrid> {
    /**
     * parameters that may be changed for cell behavior
     **/
    double prolif_scale_factor = 0.3; //Correction for appropriate proliferation rate
    double KERATINO_EGF_CONSPUMPTION = -0.005; //consumption rate by keratinocytes
    double KERATINO_APOPTOSIS_EGF = 0.005; //level at which apoptosis occurs by chance (above this and no apoptosis)
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
    float r;
    float g;
    float b;
    // Clonal dynamic tracking
    EpidermisCellGenome myGenome = new EpidermisCellGenome(); // Creating genome class within each cell
    int parentID;
    int cellID;
    int ParentCloneID; // Tracking parent Clone ID
    int cloneID; // Needs to track the cell so only unique populations have who they came from and the same cell ID for same genome population...
    static int cloneCounter = 1;
    static int cellIDcounter = 0;

    public void init(int cellType, float r, float g, float b, EpidermisCellGenome parentGenome, int parentinfo, int cloneInfo, int parentCloneInfo) { //This initilizes an agent with whatever is inside of this function...
        this.myType = cellType;
        this.r = r;
        this.b = b;
        this.g = g;
        this.parentID = parentinfo; // Sets the parentID of what is passed to the newly created cell...
        this.cellIDcounter += 1; // Add 1 to set the cell ID to a unique value...
        this.cellID = cellIDcounter; // Set the cell ID for the newly created cell to a unique value...
        this.cloneID = cloneInfo;
        this.ParentCloneID = parentCloneInfo;
        this.Action = STATIONARY;

        // copying over the genome from the parentCell
        myGenome = new EpidermisCellGenome();
        for (int i = 0; i < parentGenome.genomelength; i++) {
            for (int p = 0; p < parentGenome.mut_pos[i].size(); p++) {
                myGenome.mut_pos_setter(i, parentGenome.mut_pos_getter(i, p));
            }
        }
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
    public int basalProlif(){
        double divideWhere = G().RN.nextDouble();
        if(divideWhere<=1.0/3){
            return 2; // Dividing up
        } else if(divideWhere>(2.0/3)){
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

        if(y==0){
            int divOptions = GetEmptyVNSquares(x, y, false, G().divHoodBasal, G().inBounds); // Number of coordinates you could divide into
            iDivLoc = basalProlif(); // Where the new cell is going to be (which index) if basal cell
            //TODO STOP Pushing of Melanocytes!!!!
            if(iDivLoc==0||iDivLoc==1){
                loss_count_basal+=1;
            }
            boolean Pushed = CellPush(iDivLoc);
            if(Pushed==false){
                return false; // Only false if melanocyte there
            }

        } else{
            int divOptions = GetEmptyVNSquares(x, y, true, G().divHood, G().inBounds); // Number of coordinates you could divide into
            if(divOptions>0){iDivLoc = G().RN.nextInt(divOptions);} else {return false;} //Where the new cell is going to be (which coordinate)
        }

        EpidermisCell newCell = G().NewAgent(G().inBounds[iDivLoc]);

        if (y == 0) {
            pro_count_basal++;
        }

        if(myType==KERATINOCYTE){
            int[] MutationsObtained = new int[GeneMutations.length];
            for(int j=0; j<GeneMutations.length; j++){
                if (j!=0){
                    Poisson poisson_dist = new Poisson(GeneMutations[j], RNEngine); // Setup the Poisson distributions for each gene.
                    int mutations = poisson_dist.nextInt(); // Gets how many mutations will occur for each gene
                    for(int hits=0; hits<mutations; hits++){
                        long index = ThreadLocalRandom.current().nextLong(genelengths[j]);
                        String mutout = G().GetTick() + "." + index;
                        myGenome.mut_pos_setter(j, mutout);
                        r = G().RN.nextFloat() * 0.9f + 0.1f;
                        g = G().RN.nextFloat() * 0.9f + 0.1f;
                        b = G().RN.nextFloat() * 0.9f + 0.1f;
                        ParentCloneID = cloneID;
                        cloneCounter += 1; // Place here if only tracking cells with mutations that hit the 71 genes of interest...
                        cloneID = cloneCounter;
                        String[] parentLineage = G().lineages.get(ParentCloneID);
                        String[] myLineage = Arrays.copyOf(parentLineage, parentLineage.length + 1);
                        myLineage[parentLineage.length] = ParentCloneID + "." + cloneID;
                        G().lineages.add(myLineage);
                    }
                }
            }
        }


        newCell.init(myType, r, g, b, myGenome, cellID, cloneID, ParentCloneID); // initializes a new skin cell, pass the cellID for a new value each time.
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
            //if(c.Ysq()>= G().yDim-1){c.itDead();}
            return true;
        } else{
            return false;
        }
    }

    // Sets the coordinates for a cell that is moving.
    public int GetMoveCoords() {
        int iMoveCoord=-1;  //when it's time to move, it is the index of coordinate that is picked from Coords array above. -1 == Not Moving
        int MoveOptions=GetEmptyVNSquares(Xsq(),Ysq(),true, G().divHood, G().inBounds);
        if(MoveOptions>0&&myType==KERATINOCYTE) {
            iMoveCoord=G().RN.nextInt(MoveOptions);
        }
        return iMoveCoord;
    }

    public void itDead(){
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
        if (myType == KERATINOCYTE && G().EGF.SQgetCurr(x, y) < KERATINO_APOPTOSIS_EGF && G().RN.nextDouble() > Math.pow(G().EGF.SQgetCurr(x, y) / KERATINO_APOPTOSIS_EGF,3)) {
            //DEATH FROM LACK OF NUTRIENTS KERATINOCYTE
            itDead();
            return;
        }

        if (G().RN.nextFloat() >= 0.0) {
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

    // Builds my genome information for data analysis
    String ToString(){
        String cellInfo="{["+createStrID()+"];[";
        for(int iGene=0;iGene<myGenome.genomelength;iGene++){
            cellInfo+="[";
            for(int iMut=0;iMut<myGenome.mut_pos[iGene].size();iMut++){
                cellInfo+=myGenome.mut_pos_getter(iGene,iMut)+",";
            }
            cellInfo+="],";
        }
        cellInfo+="]}";
        return cellInfo;
    }

    // Creates a unique string ID using parentID and cellID
    private String createStrID(){
        String strID=parentID + "." + cellID;
        return strID;
    }

}
