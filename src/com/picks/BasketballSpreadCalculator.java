package com.picks;

import java.util.*;
import java.util.stream.Collectors;

public class BasketballSpreadCalculator {

    private static class GameInfo {
        String result;
        int teamScore;
        int opponentScore;
        int differential;
        int totalPoints;

        boolean isMaxDiff;
        boolean isMinDiff;
        boolean isMaxTotal;
        boolean isMinTotal;

        GameInfo(String result, int teamScore, int opponentScore) {
            this.result = result;
            this.teamScore = teamScore;
            this.opponentScore = opponentScore;
            this.differential = result.equals("Win") ? teamScore - opponentScore : opponentScore - teamScore;
            this.totalPoints = teamScore + opponentScore;
        }

        @Override
        public String toString() {
            return result + ", " + teamScore + "-" + opponentScore;
        }
    }

    private List<GameInfo> teamAGames;
    private List<GameInfo> teamBGames;
    private int teamAodds;
    private int teamBodds;

    public BasketballSpreadCalculator(String teamAData, String teamBData, int teamAodds, int teamBodds) {
        this.teamAGames = processGameData(teamAData);
        this.teamBGames = processGameData(teamBData);
        this.teamAodds = teamAodds;
        this.teamBodds = teamBodds;
    }

    public double calculateSpread() {
        double xAvg = calculateAverage(teamAGames);
        double yAvg = calculateAverage(teamBGames);
    
        reportPerformanceAnalysis();
        return calculateSpread(xAvg, yAvg);
    }
    

    private void reportPerformanceAnalysis() {
        // Set max/min flags for differential and total for each game in both teams
        setMaxMinFlags(teamAGames);
        setMaxMinFlags(teamBGames);
    
        // Print headers for team statistics
        System.out.println("              Odds | Wins | Losses | Avg Diff | Max Diffs | Min Diffs");
        printTeamStats("TEAM A", teamAGames, teamAodds);
        printTeamStats("TEAM B", teamBGames, teamBodds);
    
        
        // Print details for each game of Team A
        System.out.println("TEAM A Games:");
        System.out.println("Result | Score       | Diff | Total  | isMaxDiff    | isMinDiff    | isMaxTotal   | isMinTotal  |");
        teamAGames.forEach(game -> printGameDetails(game, teamAGames));
    
        // Print details for each game of Team B
        System.out.println("\nTEAM B Games:");
        System.out.println("Result | Score       | Diff | Total  | isMaxDiff    | isMinDiff    | isMaxTotal   | isMinTotal  |");
        teamBGames.forEach(game -> printGameDetails(game, teamBGames));
    }
    

    private void setMaxMinFlags(List<GameInfo> games) {
        int maxDiff = games.stream().mapToInt(g -> g.differential).max().orElse(0);
        int minDiff = games.stream().mapToInt(g -> g.differential).min().orElse(0);
        int maxTotal = games.stream().mapToInt(g -> g.totalPoints).max().orElse(0);
        int minTotal = games.stream().mapToInt(g -> g.totalPoints).min().orElse(0);

        for (GameInfo game : games) {
            game.isMaxDiff = game.differential == maxDiff;
            game.isMinDiff = game.differential == minDiff;
            game.isMaxTotal = game.totalPoints == maxTotal;
            game.isMinTotal = game.totalPoints == minTotal;
        }
    }

    
    private void printTeamStats(String teamName, List<GameInfo> games, int odds) {
        long wins = games.stream().filter(g -> g.result.equals("Win")).count();
        long losses = games.size() - wins;
        double avgDiff = games.stream().mapToInt(g -> g.differential).average().orElse(0);
    
        String maxDiffs = getFormattedMaxMinStats(games, true);  // For Max Diffs
        String minDiffs = getFormattedMaxMinStats(games, false); // For Min Diffs
    
        System.out.println(String.format("%s | %8d | %5d | %6d | %8.2f | %10s | %10s",
                teamName,
                odds,
                wins,
                losses,
                avgDiff,
                maxDiffs,
                minDiffs
        ));
    }
    
    
    
    private String getFormattedMaxMinStats(List<GameInfo> games, boolean isMax) {
        OptionalInt maxMinValue = isMax ? games.stream().mapToInt(g -> g.differential).max() :
                                          games.stream().mapToInt(g -> g.differential).min();
    
        if (maxMinValue.isPresent()) {
            int value = maxMinValue.getAsInt();
            return games.stream()
                        .filter(g -> g.differential == value)
                        .map(g -> "Y (" + value + ")")
                        .distinct()
                        .collect(Collectors.joining(", "));
        } else {
            return "N/A"; // No games or no max/min differential
        }
    }
    

    private void printGameDetails(GameInfo game, List<GameInfo> games) {
        String maxDiffLabel = getOrdinalLabel(games, game.differential, true, true);
        String minDiffLabel = getOrdinalLabel(games, game.differential, false, true);
        String maxTotalLabel = getOrdinalLabel(games, game.totalPoints, true, false);
        String minTotalLabel = getOrdinalLabel(games, game.totalPoints, false, false);
    
        System.out.println(String.format("%-6s | %5d-%-5d | %4d | %6d | %12s | %12s | %12s | %12s",
                game.result,
                game.teamScore,
                game.opponentScore,
                game.differential,
                game.totalPoints,
                maxDiffLabel,
                minDiffLabel,
                maxTotalLabel,
                minTotalLabel
        ));
    }
    
    

    private String getOrdinalLabel(List<GameInfo> games, int value, boolean isMax, boolean isDifferential) {
        List<Integer> values = games.stream()
                                    .mapToInt(g -> isDifferential ? g.differential : g.totalPoints)
                                    .boxed()
                                    .sorted(isMax ? Comparator.reverseOrder() : Comparator.naturalOrder())
                                    .collect(Collectors.toList());
    
        int rank = values.indexOf(value) + 1;
        if (rank > 0) {
            switch (rank) {
                case 1: return "1st (" + value + ")";
                case 2: return "2nd (" + value + ")";
                case 3: return "3rd (" + value + ")";
                default: return ""; // or some default label
            }
        }
        return ""; // Value not found or no ranking
    }
    
    

    private List<GameInfo> processGameData(String data) {
        List<GameInfo> games = new ArrayList<>();
        String[] lines = data.trim().split("\n");
        for (int i = 1; i < lines.length; i++) {
            String[] parts = lines[i].split(", ");
            String[] scores = parts[1].split("-");
            int teamScore = Integer.parseInt(scores[0]);
            int opponentScore = Integer.parseInt(scores[1]);
            games.add(new GameInfo(parts[0], teamScore, opponentScore));
        }
        return games;
    }

    private double calculateAverage(List<GameInfo> games) {
        return games.stream().mapToInt(game -> game.differential).average().orElse(0);
    }

    private double calculateSpread(double xAvg, double yAvg) {
        if (xAvg > 0 && yAvg > 0) {
            return (xAvg - yAvg) / 2;
        } else if (yAvg > 0 && xAvg < 0) {
            return (yAvg + Math.abs(xAvg)) / 2;
        } else if (xAvg < 0 && yAvg < 0) {
            return (Math.abs(yAvg) - Math.abs(xAvg)) / 2;
        } else if (xAvg > 0 && yAvg < 0) {
            return (xAvg + Math.abs(yAvg)) / 2;
        }
        return 0; // Fallback case
    }

}
