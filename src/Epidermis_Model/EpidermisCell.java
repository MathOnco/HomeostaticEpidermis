package Epidermis_Model;

import AgentFramework.AgentSQ2;

import static AgentFramework.Utils.ModWrap;
import static Epidermis_Model.EpidermisConst.*;

/**
 * Created by schencro on 3/31/17.
 */


class EpidermisCell extends AgentSQ2<EpidermisGrid> {
    /**
     * parameters that may be changed for cell behavior
     **/
    double prolif_scale_factor = 0.8; //Correction for appropriate proliferation rate
    double KERATINO_EGF_CONSPUMPTION = -0.005; //consumption rate by keratinocytes
    double MELANO_BFGF_CONSUMPTION = -0.01; //consumption rate by melanocytes
    double KERATINO_APOPTOSIS_EGF = 0.01; //level at which apoptosis occurs by chance (above this and no apoptosis)
    double MELANO_APOPTOSIS_BFGF = 0.1; //check line above, same for melanocytes
    int MELANO_DIV_DENSITY_MIN = 16; //this number or fewer keratinocytes around melanocyte and division won't happen; melanin unit
    static int pro_count = 0;
    static int pro_count_basal = 0;
    static int loss_count_basal = 0;
    static int death_count = 0;
    static int myType; //cell type
    int Action; //cells action

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
        myType = cellType;
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
    public int GetEmptyVNSquares(int x, int y, boolean OnlyEmpty, int[] inBounds){
        int finalCount=0;
        int inBoundsCount = G().SQsToLocalIs(G().divHood, inBounds, x, y, true, false); // Gets all inbound indices
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

    public int CountInArea(int x, int y, int searchRadius, int cellType) {
        //gets count of cells of type in a square around x,y (in a 9 x 9 block (4 in every direction))
        int count = 0;
        for (int xDisp = -searchRadius; xDisp <= searchRadius; xDisp++) {
            for (int yDisp = -searchRadius; yDisp <= searchRadius; yDisp++) {
                int searchX = ModWrap(xDisp + x, G().xDim);
                int searchY = yDisp + y;
                if (G().In(searchX, searchY)) {
                    EpidermisCell c = (EpidermisCell) G().SQtoAgent(searchX, searchY); // SQtoAgent returns null if no one there
                    if (c != null && c.myType == cellType) {
                        count++;
                    }
                }
            }
        }
        return count;
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
        // If bFGF is low then next double is likely to be higher...Results in no proliferation
        if (myType == MELANOCYTE && G().RN.nextDouble() > G().BFGF.SQgetCurr(x, y) * prolif_scale_factor) {
            return false;
        }

        if (myType == MELANOCYTE && CountInArea(x, y, G().DENSITY_SEARCH_SIZE, KERATINOCYTE) <= MELANO_DIV_DENSITY_MIN) {
            return false;
        }

        if(y==0){
            int divOptions = GetEmptyVNSquares(x, y, false, G().inBounds); // Number of coordinates you could divide into
            iDivLoc = basalProlif(); // Where the new cell is going to be (which index) if basal cell

            CellPush(iDivLoc);

        } else{
            int divOptions = GetEmptyVNSquares(x, y, true, G().inBounds); // Number of coordinates you could divide into
            if(divOptions>0){iDivLoc = G().RN.nextInt(divOptions);} else {return false;} //Where the new cell is going to be (which coordinate)
        }

        EpidermisCell newCell = G().NewAgent(G().inBounds[iDivLoc]);
        Action = DIVIDE;

        if (y == 0) {
            pro_count_basal++;
        }

        //Find mutated genes given mutation rate and multinomial distribution of the proportion of gene in genome.
//        int mutationcount = poisson_dist.nextInt();// Sample from poisson distribution for mutation rate.
//        int[] hitgenes = Utils.sample(mutationcount, EpidermisCellGenome.geneproportion); // based on multinomial distribution
//        for (int i = 0; i < hitgenes.length; i++) {
//            //Add the appropriate mutation index for the gene.
//            if (hitgenes[i] != 0) {
//                //add a mutated position to the genome array from a randomly chosen position
//                int rnpos = G().RN.nextInt((int) (myGenome.genelengths[hitgenes[i]]));
//                String mutout = G().Tick() + "." + rnpos;
//
//                myGenome.mut_pos_setter(hitgenes[i], mutout);
//
//                r = G().RN.nextFloat() * 0.9f + 0.1f;
//                g = G().RN.nextFloat() * 0.9f + 0.1f;
//                b = G().RN.nextFloat() * 0.9f + 0.1f;
//                ParentCloneID = cloneID;
//                cloneCounter += 1; // Place here if only tracking cells with mutations that hit the 71 genes of interest...
//                cloneID = cloneCounter;
//                String[] parentLineage = G().lineages.get(ParentCloneID);
//                String[] myLineage = Arrays.copyOf(parentLineage, parentLineage.length + 1);
//                myLineage[parentLineage.length] = ParentCloneID + "." + cloneID;
//                G().lineages.add(myLineage);
//
//
//            } else { // Hit the rest of the genome...
//                long rnpos = ThreadLocalRandom.current().nextLong(myGenome.genelengths[hitgenes[i]]);
//                String mutout = G().Tick() + "." + rnpos;
//                myGenome.mut_pos_setter(hitgenes[i], mutout);
//            }
//        }

        newCell.init(myType, r, g, b, myGenome, cellID, cloneID, ParentCloneID); // initializes a new skin cell, pass the cellID for a new value each time.
        pro_count += 1;
        return true;
    }

    public void CellPush(int iDivLoc){
        int i = G().inBounds[iDivLoc];
        int x = G().ItoX(i);
        int y = G().ItoY(i);
        //look up for empty square
        int colTop=y;
        EpidermisCell c=G().ItoAgent(i);
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
    }

//    public void CellPush2(int x,int y){
//        //look up for empty square
//        int colTop=y;
//        SkinCell c=(SkinCell)(MyGrid().FirstOnSquare(x,colTop));
//        while(c!=null){
//            colTop++;
//            c=(SkinCell)(MyGrid().FirstOnSquare(x,colTop));
//        }
//        //kill top cell if it will hit air
//        if(colTop>=AIR_HEIGHT){
//            MyGrid().FirstOnSquare(x,colTop-1).Remove();
//            colTop--;
//        }
//        //move column of cells up
//        for(;colTop>y;colTop--){
//            c=(SkinCell)(MyGrid().FirstOnSquare(x,colTop-1));
//            c.MoveSq(x,colTop);
//        }
//    }

    // Sets the coordinates for a cell that is moving.
    public int GetMoveCoords() {
        int iMoveCoord=-1;  //when it's time to move, it is the index of coordinate that is picked from Coords array above. -1 == Not Moving
        int MoveOptions=GetEmptyVNSquares(Xsq(),Ysq(), true, G().inBounds);
        if(MoveOptions>0&&(myType==KERATINOCYTE||myType==MELANOCYTE&&CountInArea(Xsq(),Ysq(),G().DENSITY_SEARCH_SIZE,KERATINOCYTE)<=MELANO_DIV_DENSITY_MIN)) {
            iMoveCoord=G().RN.nextInt(MoveOptions);
        }
        return iMoveCoord;
    }

    public void itDead(){
        Dispose();
        death_count+=1;
        if (Ysq()==0){
            loss_count_basal++;
        }
    }

    public void CellStep(){
        int x=Xsq();int y=Ysq(); // Get discrete x and y coordinates
        if (y>=G().AIR_HEIGHT){
            itDead();
            return;
        }
        if (myType == KERATINOCYTE && G().EGF.SQgetCurr(x, y) < KERATINO_APOPTOSIS_EGF && G().RN.nextDouble() < Math.pow(G().EGF.SQgetCurr(x, y) / KERATINO_APOPTOSIS_EGF,3)) {
            //DEATH FROM LACK OF NUTRIENTS KERATINOCYTE
            itDead();
            return;
        }
        if (myType == MELANOCYTE && G().BFGF.SQgetCurr(x, y) < MELANO_APOPTOSIS_BFGF && G().RN.nextDouble() < G().BFGF.SQgetCurr(x, y) / MELANO_APOPTOSIS_BFGF) {
            //DEATH FROM LACK OF NUTRIENTS MELANOCYTE
            itDead();
            return;
        }

        int iMoveCoord=GetMoveCoords(); // -1 if not moving

        if(iMoveCoord!=-1) {
            Move(G().inBounds[iMoveCoord]); // We are moving
            Action = MOVING;
            if (Ysq()!=0 && y==0) {
                loss_count_basal++;
            }
        }

        CheckProliferate();

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
