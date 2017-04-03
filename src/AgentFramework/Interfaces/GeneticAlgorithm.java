package AgentFramework.Interfaces;

import AgentFramework.FileIO;

import static AgentFramework.Utils.*;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class MutantRunner<T extends Mutateable> implements Runnable{
    T mutant;
    double score;
    public MutantRunner(T myMutant){
        mutant=myMutant;
    }

    /**
     * executes one run of the mutant runner, intended to be called by the genetic algorithm class
     */
    public void run(){
        score=mutant.Score();
    }
}

/**
 * a genetic algorithm implementation that uses multithreading to increase evaluation
 * @param <T> the mutant class that will be evaluated and evolved
 */
public class GeneticAlgorithm  <T extends Mutateable> implements Sortable {
    ArrayList<T> agents;
    ArrayList<Integer>ids;
    ArrayList<Integer>parentIDs;
    ArrayList<Double> scores;
    int idCount;
    Random rn;
    T progenitor;
    FileIO resultsWriter;
    int generation;
    boolean running;

    /**
     * @param progenitor the object that will be copied and randomized to create the starting population
     */
    public GeneticAlgorithm(T progenitor){
        rn=new Random();
        this.progenitor=progenitor;
        agents=new ArrayList<T>();
        ids=new ArrayList<Integer>();
        parentIDs=new ArrayList<Integer>();
        scores=new ArrayList<Double>();
        this.running=false;
    }

    /**
     * used to compare runs by the quicksorter
     */
    public double Compare(int iFirst, int iSecond) {
        return scores.get(iFirst)-scores.get(iSecond);
    }

    /**
     * used by the quicksorter to sort mutants
     */
    public void Swap(int iFirst, int iSecond) {
        T temp=agents.get(iFirst);
        agents.set(iFirst,agents.get(iSecond));
        agents.set(iSecond,temp);

        double tempScore=scores.get(iFirst);
        scores.set(iFirst,scores.get(iSecond));
        scores.set(iSecond,tempScore);

        int tempID=ids.get(iFirst);
        ids.set(iFirst,ids.get(iSecond));
        ids.set(iSecond,tempID);

        int tempParentID=parentIDs.get(iFirst);
        parentIDs.set(iFirst,parentIDs.get(iSecond));
        parentIDs.set(iSecond,tempParentID);
    }


    /**
     * will stop the genetic algorithm mid-execution, after it finishes the current generation
     */
    public void Kill(){
        running=false;
    }

    /**
     * returns the number of mutants
     */
    public int Length() {
        return agents.size();
    }

    /**
     * calls the copy and mutate functions of the progenitor to create a starting population for the genetic algorithm
     * @param progenitor object that will be copied and mutated
     * @param num size of the starting population
     */
    public void CreateStartingPop(T progenitor,int num){
        idCount=0;
        agents.clear();
        ids.clear();
        parentIDs.clear();
        for(int i=0;i<num;i++){
            T addMe= (T) progenitor.Copy();
            addMe.Randomize();
            agents.add(addMe);
            parentIDs.add(-1);
            ids.add(idCount++);
        }
    }

    double RunSortPop(int nThreads,int iStart){
        if(iStart<scores.size()) {
            scores.subList(iStart, scores.size()).clear();
        }
        int len=agents.size();
        ArrayList<MutantRunner<T>> runners=new ArrayList<MutantRunner<T>>();
        ArrayList<Thread> threads=new ArrayList<Thread>();
        ExecutorService executor= Executors.newFixedThreadPool(nThreads);
        for(int i=iStart;i<agents.size();i++){
            MutantRunner<T> runner=new MutantRunner<T>(agents.get(i));
            runners.add(runner);
            threads.add(new Thread(runner));
        }
        //make threads and runners
        for(int i=0;i<threads.size();i++){
            executor.execute(threads.get(i));
        }
        executor.shutdown();
        while(!executor.isTerminated());
        //wait for threads to join
        for(int i=0;i<runners.size();i++){
            scores.add(runners.get(i).score);
        }
        QuickSort(this,true);
        //score agents and sort
        System.out.println("generation: "+generation+"HighScore: "+scores.get(0));
        for(int i=0;i<agents.size();i++){
            agents.get(i).SaveInfo(resultsWriter,generation,i,scores.get(i));
        }
        //save info
        generation++;
        return scores.get(0);
    }
    void MutatePop(int bestToKeep,double mutationMag,int nToRecombine,int len){
        parentIDs=new ArrayList<>(ids.subList(0,bestToKeep));
        ids=new ArrayList<>();
        for(int i=0;i<bestToKeep;i++){
            ids.add(idCount++);
        }
        agents.subList(bestToKeep,agents.size()).clear();
        if(agents.size()<2){
            nToRecombine=0;
        }
//        for(int i=0;i<nToRecombine;i++){
//            //recombine some
//            int[] indices=RandomIndices(bestToKeep,2,rn);
//            T parent1=agents.get(indices[0]);
//            T parent2=agents.get(indices[1]);
//            T child=(T)parent1.Recombine(parent2);
//            child.Mutate(mutationMag);
//            agents.add(child);
//        }
        for(int i=0;i<len-(bestToKeep+nToRecombine);i++){
            //mutate clones to fill the rest
            int parentToCopy=i%bestToKeep;
            T child=(T)agents.get(parentToCopy).Copy();
            child.Mutate(mutationMag);
            agents.add(child);
            ids.add(idCount++);
            parentIDs.add(parentIDs.get(parentToCopy));
        }
    }

    /**
     * Runs the genetic algorithm
     * @param nGenerations generations over which to run the genetic algorithm
     * @param popSize size of each generation
     * @param bestToKeep number of mutants to preserve across generations. these will also serve as the parents of the next generation
     * @param mutationMag fed into mutate function
     * @param rerunAll toggles whether parents should be passed unchanged into the next generation or mutated as well
     * @param resultsWriter used to compile results of the runs
     * @param nCores number of cores to run mutants on. accelerates score calculation
     * @return the best score achieved in the last generation
     */
    public String RunAlgo(int nGenerations,int popSize,int bestToKeep,double mutationMag,boolean rerunAll,FileIO resultsWriter,int nCores){
        int nToRecombine=0;//disabled for now!
        this.running=true;
        this.resultsWriter=resultsWriter;
        generation=0;
        if(bestToKeep+nToRecombine>popSize){
            throw new IllegalArgumentException("the population must be big enough to store the next generation!");
        }
        CreateStartingPop(progenitor,popSize);
        RunSortPop(nCores,0);
        int iStart=rerunAll?0:bestToKeep;
        for(int i=1;i<nGenerations-1;i++){
            MutatePop(bestToKeep,mutationMag,nToRecombine,popSize);
            RunSortPop(nCores,iStart);
            if(!running){
                return "0";
            }
        }
        double finalScore=RunSortPop(nCores,iStart);
        return Double.toString(finalScore);
    }
}
