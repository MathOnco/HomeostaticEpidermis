package AgentFramework.Interfaces;

import AgentFramework.FileIO;

/**
 * implement to allow a class to be evolved by the genetic algorithm
 */
public interface Mutateable <T>{
    public void Mutate(double magnitude);//create mutated offspring

    /**
     * returns a clone of the parent
     */
    public T Copy();

    /**
     * randomizes a clone, used for generating a starting population with high variation
     */
    public void Randomize();

//    public T Recombine(T other);//generate child by combining genomes of 2 parents

    /**
     * score function, calculates fitness of mutant
     */
    public double Score();//score mutant

    /**
     * used to save information about the mutant after a run, called by the genetic algorithm
     * @param resultsWriter the writer that will record the info for all mutants
     * @param generation the generation that the mutant is derived from
     * @param index the index of the mutant in the generation
     * @param score the score that the mutant achieved
     */
    public void SaveInfo(FileIO resultsWriter, int generation, int index, double score);

}
