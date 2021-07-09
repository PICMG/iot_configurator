package org.picmg.configurator;

public class Main {
	/*
	 * This is a workaround for JavaFX.  
	 * In order to create a runnable jar with all the required JavaFX dependencies, 
	 * the launcher class can't extend from JavaFX Application.
	 */
    public static void main(String[] args) {
        App.main(args);
    }
}
