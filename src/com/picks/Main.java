package com.picks;

public class Main {
    public static void main(String[] args) {
        // Example odds and game data (replace with actual values)
        int teamAodds = -620;
        int teamBodds = 460;
        
        String teamAData = 
        "Team A\n" +
        "Loss, 129-106\n" +
        "Win, 118-110\n" +
        "Win, 108-106\n" +
        "Loss, 112-106\n" +
        "Loss, 117-106\n" +
        "Win, 122-95\n" +
        "Loss, 115-108\n" +
        "Win, 113-92\n" +
        "Loss, 127-120\n" +
        "Win, 116-93\n" +
        "Win, 109-105\n" +
        "Win, 124-117\n" +
        "Win, 118-103\n" +
        "Loss, 102-97\n" +
        "Loss, 128-125";

    // Full game data for Team B
        String teamBData = 
        "Team B\n" +
        "Loss, 129-118\n" +
        "Loss, 127-119\n" +
        "Loss, 132-102\n" +
        "Win, 110-104\n" +
        "Loss, 130-126\n" +
        "Loss, 140-101\n" +
        "Loss, 114-90\n" +
        "Loss, 121-105\n" +
        "Loss, 136-128\n" +
        "Loss, 112-104\n" +
        "Win, 127-99\n" +
        "Loss, 129-117\n" +
        "Loss, 113-109\n" +
        "Loss, 131-127\n" +
        "Loss, 113-104";

   
        BasketballSpreadCalculator calculator = new BasketballSpreadCalculator(teamAData, teamBData, teamAodds, teamBodds);
        double spread = calculator.calculateSpread();
        System.out.println("~~~~~~~~~~~~~~~~");
        System.out.println("Calculated spread: " + spread);
    }
}