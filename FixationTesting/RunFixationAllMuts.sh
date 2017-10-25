#!/usr/bin/env bash

echo "Begin Simulations"

scriptDir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
jarFile="/out/artifacts/Homeostatic_Epidermis_jar/Homeostatic_Epidermis.jar"
outPath="FixationTesting/VAFPlotResults/"
prefix="threeDNOTCHTest.10242017."
append=".10xDim.100yrs.csv"
end=1000
# 1.0 0.9 0.8 0.7 0.6 0.5 0.4 0.3 0.2 0.1 0.0 ; <- Full list of probabilities
for p in 0.8
do
    for r in $(seq 1 $end);
    do

        echo "$p $r"
        java -jar $PWD${jarFile} ${r} ${p} ${PWD}/${outPath}${prefix}parents.replicate_${r}${append} ${PWD}/${outPath}${prefix}popSizes.replicate_${r}${append} ${PWD}/${outPath}${prefix}lineage.replicate_${r}${append} ${PWD}/${outPath}${prefix}cellPositions.replicate_${r}${append}

    done

done

echo "End Simulations"