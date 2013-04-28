package cz.cuni.mff.odcleanstore.conflictresolution;

import java.util.HashMap;
import java.util.Map;

import cz.cuni.mff.odcleanstore.conflictresolution.confidence.DecidingConfidenceCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.confidence.DummySourceConfidenceCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.confidence.MediatingConfidenceCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.confidence.SourceConfidenceCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.confidence.impl.DecidingConflictConfidenceCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.confidence.impl.MediatingConflictConfidenceCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.confidence.impl.SimpleMediatingConfidenceCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.exceptions.ResolutionFunctionNotRegisteredException;
import cz.cuni.mff.odcleanstore.conflictresolution.impl.DistanceMeasureImpl;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.AllResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.AnyResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.AvgResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.BestResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.BestSourceResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.ConcatResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.FilterResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.LongestResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.MaxResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.MaxSourceMetadataValueResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.MedianResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.MinResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.MinSourceMetadataValueResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.NoneResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.ShortestResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.TopNResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.VoteResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.WeightedVoteResolution;

/**
 * @author Jan Michelfeit
 */
public class ResolutionFunctionRegistry { // TODO interface?
    private final Map<String, ResolutionFunction> functions = new HashMap<String, ResolutionFunction>();

    public ResolutionFunction get(String functionName) throws ResolutionFunctionNotRegisteredException {
        ResolutionFunction result = functions.get(functionName);
        if (result == null) {
            throw new ResolutionFunctionNotRegisteredException(functionName);
        }
        return result;
    }

    public void register(String functionName, ResolutionFunction resolutionFunction) {
        functions.put(functionName, resolutionFunction);
    }
    
    public static ResolutionFunctionRegistry createInitialized() {
        return createInitializedWithParams(new DummySourceConfidenceCalculator(), DecidingConflictConfidenceCalculator.DEFAULT_AGREE_COEFICIENT, new DistanceMeasureImpl());
    }
    
    public static ResolutionFunctionRegistry createInitializedWithParams(SourceConfidenceCalculator sourceConfidenceCalculator) {
        return createInitializedWithParams(sourceConfidenceCalculator, DecidingConflictConfidenceCalculator.DEFAULT_AGREE_COEFICIENT, new DistanceMeasureImpl());
    }
    
    public static ResolutionFunctionRegistry createInitializedWithParams(SourceConfidenceCalculator sourceConfidenceCalculator, double agreeCoefficient) {
        return createInitializedWithParams(sourceConfidenceCalculator, agreeCoefficient, new DistanceMeasureImpl());
    }
    
    public static ResolutionFunctionRegistry createInitializedWithParams(SourceConfidenceCalculator sourceConfidenceCalculator, double agreeCoefficient, DistanceMeasure distanceMeasure) {
        ResolutionFunctionRegistry registry = new ResolutionFunctionRegistry();
        
        // Deciding resolution functions
        DecidingConfidenceCalculator decidingConflictConfidence = new DecidingConflictConfidenceCalculator(sourceConfidenceCalculator, agreeCoefficient, distanceMeasure);
        registry.register(AllResolution.getName(), new AllResolution(decidingConflictConfidence));
        registry.register(AnyResolution.getName(), new AnyResolution(decidingConflictConfidence));
        registry.register(BestResolution.getName(), new BestResolution(decidingConflictConfidence));
        registry.register(BestSourceResolution.getName(), new BestSourceResolution(decidingConflictConfidence, sourceConfidenceCalculator));
        registry.register(FilterResolution.getName(), new FilterResolution(decidingConflictConfidence));
        registry.register(LongestResolution.getName(), new LongestResolution(decidingConflictConfidence));
        registry.register(MaxResolution.getName(), new MaxResolution(decidingConflictConfidence));
        registry.register(MaxSourceMetadataValueResolution.getName(), new MaxSourceMetadataValueResolution(decidingConflictConfidence));
        registry.register(MinResolution.getName(), new MinResolution(decidingConflictConfidence));
        registry.register(MinSourceMetadataValueResolution.getName(), new MinSourceMetadataValueResolution(decidingConflictConfidence));
        registry.register(NoneResolution.getName(), new NoneResolution(decidingConflictConfidence));
        registry.register(ShortestResolution.getName(), new ShortestResolution(decidingConflictConfidence));
        registry.register(TopNResolution.getName(), new TopNResolution(decidingConflictConfidence));
        registry.register(VoteResolution.getName(), new VoteResolution(decidingConflictConfidence));
        registry.register(WeightedVoteResolution.getName(), new WeightedVoteResolution(decidingConflictConfidence, sourceConfidenceCalculator));

        // Mediating resolution functions
        MediatingConfidenceCalculator mediatingConflictConfidence = new MediatingConflictConfidenceCalculator(sourceConfidenceCalculator, distanceMeasure);
        SimpleMediatingConfidenceCalculator simpleMediatingConfidence = new SimpleMediatingConfidenceCalculator(sourceConfidenceCalculator);
        registry.register(AvgResolution.getName(), new AvgResolution(mediatingConflictConfidence));
        registry.register(ConcatResolution.getName(), new ConcatResolution(mediatingConflictConfidence));
        registry.register(MedianResolution.getName(), new MedianResolution(simpleMediatingConfidence));
        
        return registry;
    }
}
