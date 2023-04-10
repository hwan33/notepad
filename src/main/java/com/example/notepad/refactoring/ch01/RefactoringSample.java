package com.example.notepad.refactoring.ch01;

import java.util.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class RefactoringSample {

  public static String statement(Invoice invoice, Map<String, Play> plays) {
    return renderPlainText(createStatementData(invoice, plays));
  }

  private static StatementData createStatementData(Invoice invoice, Map<String, Play> plays) {
    var statementData = new StatementData();
    statementData.setCustomerName(invoice.customerName);
    updatePerformance(invoice, plays, statementData);
    statementData.setTotalAmount(totalAmount(statementData));
    statementData.setTotalVolumeCredits(totalVolumeCredits(statementData));
    return statementData;
  }

  private static void updatePerformance(
      Invoice invoice, Map<String, Play> plays, StatementData statementData) {
    statementData.setPerformances(
        invoice.performances.stream().map(i -> i.createVo(plays, i)).toList());
    statementData.getPerformances().forEach(i -> i.updateAmount(PerformanceCalculator.amountFor(i)));
    statementData.getPerformances().forEach(i -> i.updateVolumeCredits(PerformanceCalculator.volumeCreditsFor(i)));
  }

  private static String renderPlainText(StatementData statementData) {
    StringBuilder result =
        new StringBuilder("청구 내역 고객명 : " + statementData.getCustomerName() + '\n');

    for (var perf : statementData.getPerformances()) {
      result
          .append(perf.getPlay().getName())
          .append(": ")
          .append(perf.getAmount())
          .append("원, ")
          .append(perf.audience)
          .append("석\n");
    }

    result.append("총액: ").append(statementData.getTotalAmount()).append("원\n");
    result.append("적립 포인트: ").append(statementData.getTotalVolumeCredits()).append("점\n");
    return result.toString();
  }

  private static int totalAmount(StatementData statementData) {
    return statementData.getPerformances().stream()
        .map(PerformanceVo::getAmount)
        .mapToInt(i -> i)
        .sum();
  }

  private static int totalVolumeCredits(StatementData statementData) {
    return statementData.getPerformances().stream()
        .map(PerformanceVo::getVolumeCredits)
        .mapToInt(i -> i)
        .sum();
  }

  public static void main(String[] args) {
    List<Performance> performances = new ArrayList<>();
    performances.add(new Performance("hamlet", 55));
    performances.add(new Performance("as-like", 35));
    performances.add(new Performance("othello", 40));

    Invoice invoice = new Invoice("BigCo", performances);
    Map<String, Play> plays = new HashMap<>();
    plays.put("hamlet", new Play("Hamlet", "tragedy"));
    plays.put("as-like", new Play("As You Like It", "comedy"));
    plays.put("othello", new Play("Othello", "tragedy"));

    System.out.println(statement(invoice, plays));
  }

  @NoArgsConstructor
  @AllArgsConstructor
  @Getter
  @Setter
  private static class StatementData {
    private String customerName;
    private List<PerformanceVo> performances;
    private int totalAmount;
    private int totalVolumeCredits;
  }

  @AllArgsConstructor
  @Getter
  private static class Play {
    private String name;
    private String type;
  }

  @AllArgsConstructor
  @Getter
  static class Invoice {
    private String customerName;
    private List<Performance> performances;
  }

  @AllArgsConstructor
  @Getter
  static class Performance {
    private String playId;
    private int audience;

    public PerformanceVo createVo(Map<String, Play> plays, Performance performance) {
      return new PerformanceVo(this.playId, this.audience, PerformanceCalculator.playFor(plays, performance), 0, 0);
    }
  }

  @AllArgsConstructor
  @Getter
  static class PerformanceVo {
    private String playId;
    private int audience;
    private Play play;
    private int amount;
    private int volumeCredits;

    public void updateAmount(int amount) {
      this.amount = amount;
    }

    public void updateVolumeCredits(int volumeCredits) {
      this.volumeCredits = volumeCredits;
    }
  }

  static class PerformanceCalculator {
    static int volumeCreditsFor(PerformanceVo perf) {
      int result = 0;
      result += Math.max(perf.audience - 30, 0);
      if ("comedy".equals(perf.getPlay().getType())) {
        result += perf.audience / 5;
      }
      return result;
    }

    static Play playFor(Map<String, Play> plays, Performance perf) {
      return plays.get(perf.playId);
    }

    static int amountFor(PerformanceVo perf) {
      var result = 0;

      switch (perf.getPlay().getType()) {
        case "tragedy" -> {
          result = 40000;
          if (perf.audience > 30) {
            result += 1000 * (perf.audience - 30);
          }
        }
        case "comedy" -> {
          result = 30000;
          if (perf.audience > 20) {
            result += 10000 + 500 * (perf.audience - 20);
          }
          result += 300 * perf.audience;
        }
        default -> throw new RuntimeException("알 수 없는 장르");
      }
      return result;
    }
  }

  static class TragedyCalculator extends PerformanceCalculator {

  }

  static class ComedyCalculator extends PerformanceCalculator {

  }
}
