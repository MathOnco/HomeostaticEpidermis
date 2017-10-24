#!/usr/bin/env bash

echo "Begin Simulations"

scriptDir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
jarFile="/out/artifacts/Homeostatic_Epidermis_jar/Homeostatic_Epidermis.jar"
end=17400
# 1.0 0.9 0.8 0.7 0.6 0.5 0.4 0.3 0.2 0.1 0.0 ; <- Full list of probabilities
for p in 1.0
do
    for r in $(seq 16701 $end);
    do

        echo "$p $r"
        java -jar $PWD$jarFile $r $p > $scriptDir/Prob.$p.$r.tmpRep.txt

    done

    cat $scriptDir/Prob.$p.*.tmpRep.txt > $scriptDir/Prob.$p.1000reps.txt
    #rm $scriptDir/Prob.$p.*.tmpRep.txt

done

#cat $scriptDir/Prob.*.1000reps.txt > $scriptDir/FixationSimulations.$p.10192017.txt
#rm $scriptDir/Prob.*.1000reps.txt

echo "End Simulations"