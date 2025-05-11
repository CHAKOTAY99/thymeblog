package org.chak;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        try {
            new PageBuilder().buildSite();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}