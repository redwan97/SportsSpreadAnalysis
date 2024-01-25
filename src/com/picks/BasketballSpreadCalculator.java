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
        // Calculate max and min values for differential and total points
        setMaxMinFlags(teamAGames);
        setMaxMinFlags(teamBGames);

        // Reporting for Team A and Team B
        System.out.println("\t\tOdds | Wins | Losses | Avg Diff | Max Diffs | Min Diffs");
        printTeamStats("TEAM A", teamAGames, teamAodds);
        printTeamStats("TEAM B", teamBGames, teamBodds);

        System.out.println("\nGames");
        System.out.println("Result | Score       | Diff | isMaxDiff | isMinDiff | isMaxTotal | isMinTotal");
        teamAGames.forEach(this::printGameDetails);

        System.out.println("\nGames");
        System.out.println("Result | Score       | Diff | isMaxDiff | isMinDiff | isMaxTotal | isMinTotal");
        teamBGames.forEach(this::printGameDetails);
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
    
        System.out.println(String.format("%s | %7d | %6d | %6d | %8.2f | %9s | %9s",
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
    

    private void printGameDetails(GameInfo game) {
        System.out.println(String.format("%-6s | %5d-%-5d | %4d | %9s | %9s | %10s | %10s",
                game.result,
                game.teamScore,
                game.opponentScore,
                game.differential,
                game.isMaxDiff ? "Y (" + game.differential + ")" : "N",
                game.isMinDiff ? "Y (" + game.differential + ")" : "N",
                game.isMaxTotal ? "Y (" + game.totalPoints + ")" : "N",
                game.isMinTotal ? "Y (" + game.totalPoints + ")" : "N"
        ));
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
