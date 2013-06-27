package cz.cuni.mff.odcleanstore.conflictresolution;

import java.util.HashMap;
import java.util.Map;

import cz.cuni.mff.odcleanstore.conflictresolution.exceptions.ResolutionFunctionNotRegisteredException;
import cz.cuni.mff.odcleanstore.conflictresolution.impl.DistanceMeasureImpl;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.DecidingFQualityCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.DummySourceQualityCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.MediatingFQualityCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.SourceQualityCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.impl.DecidingConflictFQualityCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.impl.MediatingModeratingFQualityCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.impl.MediatingScatteringFQualityCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.AllResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.AnyResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.AvgResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.BestResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.BestSourceResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.CertainResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.ChooseSourceResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.ConcatResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.FilterResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.LongestResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.MaxResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.MaxSourceMetadataValueResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.MedianResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.MinResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.MinSourceMetadataValueResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.NoneResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.ODCSLatestResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.ShortestResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.SumResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.TopNResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.TresholdResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.VoteResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.WeightedVoteResolution;

/**
 * @author Jan Michelfeit
 */
public class ResolutionFunctionRegistry {
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
        return createInitializedWithParams(new DummySourceQualityCalculator(), DecidingConflictFQualityCalculator.DEFAULT_AGREE_COEFICIENT, new DistanceMeasureImpl());
    }
    
    public static ResolutionFunctionRegistry createInitializedWithParams(SourceQualityCalculator sourceQualityCalculator) {
        return createInitializedWithParams(sourceQualityCalculator, DecidingConflictFQualityCalculator.DEFAULT_AGREE_COEFICIENT, new DistanceMeasureImpl());
    }
    
    public static ResolutionFunctionRegistry createInitializedWithParams(SourceQualityCalculator sourceQualityCalculator, double agreeCoefficient) {
        return createInitializedWithParams(sourceQualityCalculator, agreeCoefficient, new DistanceMeasureImpl());
    }
    
    public static ResolutionFunctionRegistry createInitializedWithParams(SourceQualityCalculator sourceQualityCalculator, double agreeCoefficient, DistanceMeasure distanceMeasure) {
        ResolutionFunctionRegistry registry = new ResolutionFunctionRegistry();
        
        // Deciding resolution functions
        DecidingFQualityCalculator decidingConflictFQuality = new DecidingConflictFQualityCalculator(sourceQualityCalculator, agreeCoefficient, distanceMeasure);
        registry.register(AllResolution.getName(), new AllResolution(decidingConflictFQuality));
        registry.register(AnyResolution.getName(), new AnyResolution(decidingConflictFQuality));
        registry.register(BestResolution.getName(), new BestResolution(decidingConflictFQuality));
        registry.register(BestSourceResolution.getName(), new BestSourceResolution(decidingConflictFQuality, sourceQualityCalculator));
        registry.register(CertainResolution.getName(), new CertainResolution(decidingConflictFQuality));
        registry.register(FilterResolution.getName(), new FilterResolution(decidingConflictFQuality));
        registry.register(LongestResolution.getName(), new LongestResolution(decidingConflictFQuality));
        registry.register(MaxResolution.getName(), new MaxResolution(decidingConflictFQuality));
        registry.register(MaxSourceMetadataValueResolution.getName(), new MaxSourceMetadataValueResolution(decidingConflictFQuality));
        registry.register(MinResolution.getName(), new MinResolution(decidingConflictFQuality));
        registry.register(MinSourceMetadataValueResolution.getName(), new MinSourceMetadataValueResolution(decidingConflictFQuality));
        registry.register(NoneResolution.getName(), new NoneResolution(decidingConflictFQuality));
        registry.register(ChooseSourceResolution.getName(), new ChooseSourceResolution(decidingConflictFQuality));
        registry.register(ShortestResolution.getName(), new ShortestResolution(decidingConflictFQuality));
        registry.register(TopNResolution.getName(), new TopNResolution(decidingConflictFQuality));
        registry.register(TresholdResolution.getName(), new TresholdResolution(decidingConflictFQuality));
        registry.register(VoteResolution.getName(), new VoteResolution(decidingConflictFQuality));
        registry.register(WeightedVoteResolution.getName(), new WeightedVoteResolution(decidingConflictFQuality, sourceQualityCalculator));

        // Mediating resolution functions
        MediatingFQualityCalculator mediatingModeratingFQuality = new MediatingModeratingFQualityCalculator(sourceQualityCalculator, distanceMeasure);
        MediatingScatteringFQualityCalculator scatteringMediatingFQuality = new MediatingScatteringFQualityCalculator(sourceQualityCalculator);
        registry.register(AvgResolution.getName(), new AvgResolution(mediatingModeratingFQuality));
        registry.register(SumResolution.getName(), new SumResolution(mediatingModeratingFQuality));
        registry.register(ConcatResolution.getName(), new ConcatResolution(scatteringMediatingFQuality));
        registry.register(MedianResolution.getName(), new MedianResolution(scatteringMediatingFQuality));
        
        // ODCS-specific resolution functions
        registry.register(ODCSLatestResolution.getName(), new ODCSLatestResolution(decidingConflictFQuality));
        
        return registry;
    }
}
