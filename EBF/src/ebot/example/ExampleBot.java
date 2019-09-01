package ebot.example;

import java.io.IOException;
import java.util.Locale;
import java.util.Scanner;

import ebot.Context;
import ebot.EBot;
import ebot.ListContext;
import ebot.ebf.EBFTree;
import ebot.ebs.WorldInterface;
import ebot.io.FileDataSource;
import ebot.text.ComplexTextComparer;

public class ExampleBot {

	public static void main(String[] args) throws IOException {
		System.out.println("Loading tree.");
		
		EBFTree tree = EBFTree.parse(new FileDataSource("exampleBot.ebf"));
		
		System.out.println("Loading bot.");
		
		EBot bot = new EBot(tree, new ComplexTextComparer());
		
		bot.load();
		
		WorldInterface wi = new WorldInterface() {
			@Override
			public Locale getLocale() {
				return Locale.getDefault();
			}

			@Override
			public void couldNotFind() {
				System.out.println("I am sorry. I do not know what you mean.");
			}

			@Override
			public void out(String s) {
				System.out.println(s);
			}
		};
		
		Context c = new ListContext(System.getProperty("user.name"));
		
		Scanner s = new Scanner(System.in);
		
		bot.onConnect(wi, c);
		
		while (true) {
			System.out.print("> ");
			
			while (!s.hasNextLine()) {
				Thread.yield();
			}
			
			String line = s.nextLine();
			
			if(line.trim().toLowerCase().equals("exit")) {
				s.close();
				return;
			}
			
			bot.doTask(line, wi, c);
		}
	}

}
