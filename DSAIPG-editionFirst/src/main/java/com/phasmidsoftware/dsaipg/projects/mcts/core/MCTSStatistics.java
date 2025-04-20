
package com.phasmidsoftware.dsaipg.projects.mcts.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for tracking MCTS performance statistics.
 */
public class MCTSStatistics {
    // Runtime metrics
    private List<Long> runTimes = new ArrayList<>();
    
    // Success metrics
    private int mctsGamesPlayed = 0;
    private int mctsWins = 0;
    private int draws = 0;
    
    // Iteration performance tracking
    private Map<Integer, List<Boolean>> iterationSuccessMap = new HashMap<>();
    private Map<Integer, List<Long>> iterationTimeMap = new HashMap<>();
    
    // Track when MCTS fails to find a good move
    private int mctsFallbackCount = 0;
    
    // Current runtime measurement
    private long startTime = 0;
    
    /**
     * Start timing an MCTS search.
     */
    public void startTiming() {
        startTime = System.nanoTime();
    }
    
    /**
     * Stop timing and record the runtime.
     * @param iterations Number of iterations used in this MCTS search
     * @return Elapsed time in milliseconds
     */
    public long stopTiming(int iterations) {
        long endTime = System.nanoTime();
        long elapsedTime = (endTime - startTime) / 1_000_000; // Convert to milliseconds
        runTimes.add(elapsedTime);
        
        // Record time for this iteration count
        iterationTimeMap.computeIfAbsent(iterations, k -> new ArrayList<>())
                       .add(elapsedTime);
        
        return elapsedTime;
    }
    
    /**
     * Record the result of an MCTS-based game.
     */
    public void recordGameResult(boolean mctsWin, boolean isDraw, int iterations) {
        mctsGamesPlayed++;
        if (isDraw) {
            draws++;
        } else if (mctsWin) {
            mctsWins++;
        }
        
        // Record success for this iteration count
        if (!isDraw) {
            iterationSuccessMap.computeIfAbsent(iterations, k -> new ArrayList<>())
                            .add(mctsWin);
        }
    }
    
    /**
     * Record when MCTS couldn't find a good move and fell back to random.
     */
    public void recordMCTSFallback() {
        mctsFallbackCount++;
    }
    
    /**
     * Calculate the win rate of MCTS.
     */
    public double getWinRate() {
        if (mctsGamesPlayed == 0) return 0;
        return (double) mctsWins / mctsGamesPlayed;
    }
    
    /**
     * Calculate the success rate of MCTS (how often it finds a valid move).
     */
    public double getMCTSSuccessRate() {
        int totalMCTSSearches = runTimes.size();
        if (totalMCTSSearches == 0) return 0;
        return (double) (totalMCTSSearches - mctsFallbackCount) / totalMCTSSearches;
    }
    
    /**
     * Generate a comprehensive report of MCTS performance.
     */
    public String generateReport() {
        StringBuilder report = new StringBuilder();
        report.append("\n===== MCTS PERFORMANCE STATISTICS =====\n");
        
        // Game results
        report.append("Games played: ").append(mctsGamesPlayed).append("\n");
        report.append("MCTS wins: ").append(mctsWins)
              .append(" (").append(String.format("%.1f%%", getWinRate() * 100)).append(")\n");
        report.append("Draws: ").append(draws)
              .append(" (").append(String.format("%.1f%%", (double) draws / Math.max(1, mctsGamesPlayed) * 100)).append(")\n");
        
        // MCTS performance
        int totalMCTSSearches = runTimes.size();
        report.append("Total MCTS searches: ").append(totalMCTSSearches).append("\n");
        report.append("Failed searches: ").append(mctsFallbackCount)
              .append(" (").append(String.format("%.1f%%", (double) mctsFallbackCount / Math.max(1, totalMCTSSearches) * 100)).append(")\n");
        
        // Runtime statistics
        if (!runTimes.isEmpty()) {
            double avgTime = runTimes.stream().mapToLong(Long::longValue).average().orElse(0);
            long maxTime = runTimes.stream().mapToLong(Long::longValue).max().orElse(0);
            long minTime = runTimes.stream().mapToLong(Long::longValue).min().orElse(0);
            
            report.append("MCTS runtime (ms): avg=").append(String.format("%.1f", avgTime))
                  .append(", min=").append(minTime)
                  .append(", max=").append(maxTime).append("\n");
        }
        
        // Iteration performance analysis
        report.append("\nIteration Performance Analysis:\n");
        report.append("Iterations | Success Rate | Avg Time (ms)\n");
        report.append("---------------------------------------\n");
        
        List<Integer> sortedIterations = new ArrayList<>(iterationSuccessMap.keySet());
        sortedIterations.sort(null);
        
        for (int iteration : sortedIterations) {
            List<Boolean> successes = iterationSuccessMap.get(iteration);
            List<Long> times = iterationTimeMap.getOrDefault(iteration, new ArrayList<>());
            
            if (successes != null && !successes.isEmpty()) {
                double successRate = successes.stream().filter(s -> s).count() / (double) successes.size();
                double avgIterationTime = times.stream().mapToLong(Long::longValue).average().orElse(0);
                
                report.append(String.format("%-10d | %-11.1f%% | %-12.1f\n", 
                                        iteration, 
                                        successRate * 100,
                                        avgIterationTime));
            }
        }
        
        report.append("\n=========================================\n");
        return report.toString();
    }
}