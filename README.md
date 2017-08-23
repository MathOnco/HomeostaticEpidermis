# Epidermis_Project_Final
Epidermis_Project with the final framework

           PSF    EGF_CONS    APOPEGF    DEATHPROB      MOVE DIVLOCPROB EGF_DIFFUSION_RATE EGFDecayRate    rlambda mean   height basalDensity
40 0.004489167 0.005504418 0.07649478 0.0003645534 0.9312262    0.80839         0.09239592  0.001035161 0.03205128  730 13.81457    0.9933333


Parameter           PSF    EGF_CONS   APOPEGF  DEATHPROB      MOVE DIVLOCPROB   rlambda     mean   height
Outdated Values    0.07610124 0.002269758 0.3358162 0.01049936 0.3657964  0.8315265 0.1638467 28.08473 14.12698

EpidermisGrid Params:
EGF_DIFFUSION_RATE
DECAY_RATE:

Parameterization One OutDated:
    PS.AddParam((Random RN)->{ // PSF
            //return RN.nextDouble()*0.2+.01; //Iteration 1, 2, 3
            //return 0.07442369; // Iteration 4
            //return RN.nextDouble()*0.99999+.00001; // Iteration 5
            //return RN.nextDouble()*0.4+0.2; // Iteration 9
            //return RN.nextDouble()*0.35+0.1; // Iteration 10
            //return RN.nextDouble()*0.25+0.005; // Iteration 12
            //return RN.nextDouble()*0.15+0.01; // Iteration 13
            //return RN.nextDouble()*0.0523508+0.06992883; //Iteration 16
            return 0.07610124;
        });
        PS.AddParam((Random RN)->{ // KerEGFConsumption
            //return RN.nextDouble()*-0.009-.001; //Iteration 1, 2, 3
            //return RN.nextDouble()*-0.99999-.00001; // Iteration 5
            //return RN.nextDouble()*-0.009-.0001; // Iteration 7
            //return RN.nextDouble()*-0.005-.0001; // Iteration 8
            //return RN.nextDouble()*-0.005-.003; // Iteration 14
            //return RN.nextDouble()*-0.0045-0.001; // Iteration 15
            //return RN.nextDouble()*-0.003853343-0.001197422; //Iteration 16
            return -0.002269758;
        });
        PS.AddParam((Random RN)->{ // ApopEGF
            //return RN.nextDouble()*0.14+0.01; //Iteration 1, 2, 3
            //return RN.nextDouble()*0.99999+.00001; // Iteration 5
//            return RN.nextDouble()*0.2000853+0.3197142; // Iteration 10 - 16
            return 0.3358162;
        });
        PS.AddParam((Random RN)->{ // DeathProb
            //return RN.nextDouble()*0.0009+.00001; //Iteration 1, 2, 3
            //return RN.nextDouble()*0.99999+.00001; // Iteration 5
            //return RN.nextDouble()*0.142+0.0; // Iteration 10
//            return RN.nextDouble()*0.0345704+0.00188303; // Iteration 16
            return 0.01049936;
        });
        PS.AddParam((Random RN)->{ // MoveProb
            //return RN.nextDouble()*0.75+0.0; //Iteration 1, 2, 3
            //return RN.nextDouble()*0.99999+.00001; // Iteration 5
            //return RN.nextDouble()*0.5+.00001; // Iteration 12
//            return RN.nextDouble()*0.4948984+0.002261338; //Iteration 16
            return 0.3657964;
        });
        PS.AddParam((Random RN)->{ // DIVLOCPROB
            //return RN.nextDouble()*0.55+.2; //Iteration 1, 2, 3
            //return RN.nextDouble()*0.99999+.00001; // Iteration 5
            //return RN.nextDouble()*0.5+.5; // Iteration 9
            //return RN.nextDouble()*0.25+0.75; // Iteration 11
//            return RN.nextDouble()*0.1843497+0.7528085; //Iteration 16
            return 0.8315265;
        });