package com.example.notepad.refactoring.ch01;

import com.example.notepad.refactoring.ch01.RefactoringSample.Invoices.Performance;
import java.util.*;

public class RefactoringSample {
  public static String statement(Invoices invoice, Map<String, Play> plays) throws Exception {
    var result = "청구 내역 고객명 : " + invoice.customerName + '\n';

    for (var perf : invoice.performances) {
      result +=
          playFor(plays, perf).name + ": " + amountFor(perf, plays) + "원, " + perf.audience + "석\n";
    }

    var volumeCredits = totalVolumeCredits(invoice, plays);
    result += "총액: " + totalAmount(invoice, plays) + "원\n";
    result += "적립 포인트: " + volumeCredits + "점\n";
    return result;
  }

  private static int totalAmount(Invoices invoice, Map<String, Play> plays) throws Exception {
    var result = 0;
    for (var perf : invoice.performances) {
      result += amountFor(perf, plays);
    }
    return result;
  }

  private static int totalVolumeCredits(Invoices invoice, Map<String, Play> plays) {
    var result = 0;
    for (var perf : invoice.performances) {
      result += volumeCreditsFor(plays, perf);
    }
    return result;
  }

  private static int volumeCreditsFor(Map<String, Play> plays, Performance perf) {
    int result = 0;
    result += Math.max(perf.audience - 30, 0);
    if ("comedy".equals(playFor(plays, perf).type)) {
      result += Math.floor(perf.audience / 5);
    }
    return result;
  }

  private static Play playFor(Map<String, Play> plays, Performance perf) {
    return plays.get(perf.playId);
  }

  private static int amountFor(Performance perf, Map<String, Play> plays) throws Exception {
    var result = 0;

    switch (playFor(plays, perf).type) {
      case "tragedy":
        result = 40000;
        if (perf.audience > 30) {
          result += 1000 * (perf.audience - 30);
        }
        break;
      case "comedy":
        result = 30000;
        if (perf.audience > 20) {
          result += 10000 + 500 * (perf.audience - 20);
        }
        result += 300 * perf.audience;
        break;
      default:
        throw new Exception("알 수 없는 장르");
    }
    return result;
  }

  public static void main(String[] args) throws Exception {
    List<Performance> performances = new ArrayList<>();
    performances.add(new Performance("hamlet", 55));
    performances.add(new Performance("as-like", 35));
    performances.add(new Performance("othello", 40));

    Invoices invoices = new Invoices("BigCo", performances);
    Map<String, Play> plays = new HashMap<>();
    plays.put("hamlet", new Play("Hamlet", "tragedy"));
    plays.put("as-like", new Play("As You Like It", "comedy"));
    plays.put("othello", new Play("Othello", "tragedy"));

    System.out.println(statement(invoices, plays));
  }

  private static class Play {
    private String name;
    private String type;

    public Play(String name, String type) {
      this.name = name;
      this.type = type;
    }
  }

  static class Invoices {
    private String customerName;
    private List<Performance> performances;

    public Invoices(String customerName, List<Performance> performances) {
      this.customerName = customerName;
      this.performances = performances;
    }

    static class Performance {
      private String playId;
      private int audience;

      public Performance(String playId, int audience) {
        this.playId = playId;
        this.audience = audience;
      }
    }
  }
}
