// Nicolas Stoian

import java.net.*;
import java.util.Arrays;
import java.util.Random;
import java.io.*;

public class GameServerThread extends Thread {

	// game object
	protected static GameServerObject game;

	// variables
    private Socket socket = null;
    private DataInputStream in;
    private DataOutputStream out;
    protected String name;
	private Object group;
	private long startTime;
	private Random r = new Random();
	protected boolean isWinner = false;
	protected boolean first = false;
	protected int score = 0;
	protected int wager = 0;

	// objects for contestant functions
	private static Object contestantFormGroup = new Object();
	private static Object contestantTakeSeat = new Object();
	private static Object contestantTakeExam = new Object();
	private static Object contestantStartTheGame = new Object();
	private static Object contestantPlayTheGame = new Object();
	private static Object contestantPlayFinal = new Object();

    public GameServerThread(Socket socket) {
        super("GameServerThread");
        this.socket = socket;
    }

    public void run() {
    	try{
            in = new DataInputStream(socket.getInputStream());
        	out = new DataOutputStream(socket.getOutputStream());
            name = in.readUTF();
        	if (name.equals("Game")){
        		game();
        	}
        	if (name.contains("Contestant")){
        		contestant();
        	}
        	if (name.equals("Announcer")){
        		announcer();
        	}
        	if (name.equals("Host")){
        		host();
        	}
        }
    	catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Game part

    public void game(){
    	try{

    		String gameType = in.readUTF();
    		if (gameType.equals("Default")){
    			game = new GameServerObject();
        		System.out.println("Welcome to Guess What or Who");
    			System.out.println("No command line arguements detected");
    			System.out.println("Starting a new game with the default settings:");
    			System.out.println("numRounds = " + game.numRounds);
    			System.out.println("numQuestions = " + game.numQuestions);
    			System.out.println("questionValues = " + game.questionValues);
    			System.out.println("rightPercent = " + game.rightPercent);
    			System.out.println("room_capacity = " + game.room_capacity);
    			System.out.println("num_contestants = " + game.num_contestants);
    			System.out.println();
        		out.writeUTF("Default game started");
    		}
    		if (gameType.equals("Custom")){
    			game = new GameServerObject();
    			game.numRounds = in.readInt();
    			game.numQuestions = in.readInt();
    			game.questionValues = in.readInt();
    			game.rightPercent = in.readDouble();
    			game.room_capacity = in.readInt();
    			game.num_contestants = in.readInt();
        		System.out.println("Welcome to Guess What or Who");
    			System.out.println("Arguements detected");
    			System.out.println("Starting a new game with custom settings:");
    			System.out.println("numRounds = " + game.numRounds);
    			System.out.println("numQuestions = " + game.numQuestions);
    			System.out.println("questionValues = " + game.questionValues);
    			System.out.println("rightPercent = " + game.rightPercent);
    			System.out.println("room_capacity = " + game.room_capacity);
    			System.out.println("num_contestants = " + game.num_contestants);
    			System.out.println();
        		out.writeUTF("Custom game started");
    		}
    	}
    	catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Contestant part

    public void contestant(){
    	try{
    		startTime = System.currentTimeMillis( );
    		game.contestants.add(this);
    		String function = "initalized";
    		while (!(function.equals("Done"))){
    			function = in.readUTF();
    			if (function.equals("formGroup")){
    				formGroup();
    				out.writeUTF("complete");
    			}
    			if (function.equals("takeSeat")){
    				takeSeat();
    				out.writeUTF("complete");
    			}
    			if (function.equals("takeExam")){
    				takeExam(game.exam_time);
    				out.writeUTF("complete");
    			}
    			if (function.equals("getTestResults")){
    				getTestResults();
    	    		if (!isWinner){ // if this contestant is not a winner they exit at this point
    	    			out.writeUTF("loser");
    	    			return;
    	    		}
    	    		out.writeUTF("complete");
    			}
    			if (function.equals("startTheGame")){
    				startTheGame();
    				out.writeUTF("complete");
    			}
    			if (function.equals("playTheGame")){
    				playTheGame();
    				out.writeUTF("complete");
    			}
    			if (function.equals("playFinal")){
    				playFinal();
    				out.writeUTF("complete");
    			}
    		}
    	}
    	catch (Exception e) {
            e.printStackTrace();
        }
    }

 // method to keep track of age
 	public long age( ){
 		return System.currentTimeMillis( ) - startTime;
 	}

 	// method to format numbers correctly for use in String name
 	public String numFormat(int num){
 		Integer i = new Integer(num);
 		String str = i.toString( );
 		if( num < 10 ){
 			str = " " + i;
 		}
 		return str;
 	}

 	// contestants form groups of room_capacity size and have their group object set to the appropriate game.groups vector object
 	public void formGroup(){
 		synchronized(contestantFormGroup){
 			if( game.groups.size( ) == 0 || game.numInGroup % game.room_capacity == 0 ){
 				game.groups.add( new Object( ));
 			}
 			group = game.groups.lastElement( );
 			game.numInGroup++;
 			System.out.println("[age = " + age( ) + "ms] " + name + " ==> Joins group " + game.groups.indexOf( game.groups.lastElement( )));
 		}
 	}

 	// contestants wait on their group object to be notified by the announcer to enter a room and take a seat
 	public void takeSeat(){
 		try {
 			synchronized(contestantTakeSeat){
 				game.numWaitingToSit++;
 				synchronized(game.waitForAllReadyToSit){
 					if (game.numWaitingToSit == game.num_contestants){
 						game.waitForAllReadyToSit.notify();
 					}
 				}
 			}
 			synchronized(group){
 				group.wait();
 			}
 		}
 		catch (InterruptedException e){
 			System.out.println(e);
 		}
 		System.out.println("[age = " + age( ) + "ms] " + name + " ==> Enters classroom " + game.groups.indexOf(group) + " and takes a seat");
 	}

 	// once all contestants are seated they take the exam_time exam
 	public void takeExam(int exam_time){
 		try {
 			synchronized(contestantTakeExam){
 				game.numSitting++;
 				synchronized(game.waitForAllSitting){
 					if (game.numSitting == game.num_contestants){
 						game.waitForAllSitting.notify();
 					}
 				}
 			}
 			synchronized(game.sittingContestants){
 				game.sittingContestants.wait();
 			}
 			System.out.println("[age = " + age( ) + "ms] " + name + " ==> Starts the exam");
 			sleep(exam_time);
 			synchronized(contestantTakeExam){
 				System.out.println("[age = " + age( ) + "ms] " + name + " ==> Finished the exam");
 				game.numFinishedExam++;
 				synchronized(game.waitForAllFinishedExam){
 					if (game.numFinishedExam == game.num_contestants){
 						game.waitForAllFinishedExam.notify();
 					}
 				}
 			}
 		}
 		catch (InterruptedException e){
 			System.out.println(e);
 		}
 	}

 	// contestants wait on themselves to be given their test results in FCFS order using the game.contestants vector
 	public void getTestResults(){
 		try {
 			synchronized(this){
 				this.wait();
 			}
 		}
 		catch (InterruptedException e){
 			System.out.println(e);
 		}
 	}

 	// the winning contestants wait for the host to start the game
 	public void startTheGame(){
 		try {
 			synchronized(game.waitForAnnouncer){
 				game.waitForAnnouncer.wait();
 			}
 			synchronized(contestantStartTheGame){
 				System.out.println("[age = " + age( ) + "ms] " + name + " ==> Ready to start the game");
 				game.numWaitingToStartGame++;
 				synchronized(game.waitToStartGame){
 					if (game.numWaitingToStartGame == 4){
 						game.waitToStartGame.notify();
 					}
 				}
 			}
 			synchronized(game.waitForHost){
 				game.waitForHost.wait();
 			}
 		}
 		catch (InterruptedException e){
 			System.out.println(e);
 		}
 	}

 	// contestants answer questions for numRounds rounds and numQuestions questions. They think for a random amount of time up to 1 second
 	// and the first one that wakes up answers
 	public void playTheGame(){
 		for(int i = 0; i < game.numRounds; i++){
 			for(int j = 0; j < game.numQuestions; j++){
 				try{
 					sleep(r.nextInt(1000));
 				}
 				catch (InterruptedException e){
 					System.out.println(e);
 				}
 				synchronized(contestantPlayTheGame){
 					if (game.answered == false){
 						game.answered = true;
 						first = true;
 					}
 					game.numFinishedThinking++;
 					synchronized(game.waitForAnswer){
 						if (game.numFinishedThinking == 4){
 							game.waitForAnswer.notify();
 						}
 					}
 				}
 				try{
 					synchronized(game.waitForNextRound){
 						game.waitForNextRound.wait();
 					}
 				}
 				catch (InterruptedException e){
 					System.out.println(e);
 				}
 			}
 		}
 	}

 	// contestants play final Guess What or Who, they wager an amount of their score which is then added to or subtracted from
 	// their score depending on if they got the final question right with a 50% chance
 	public void playFinal(){
 		try {
 			synchronized(contestantPlayFinal){
 				game.numWaitingToStartFinal++;
 				synchronized(game.waitToStartFinal){
 					if (game.numWaitingToStartFinal == 4){
 						game.waitToStartFinal.notify();
 					}
 				}
 			}
 			synchronized(this){
 				this.wait();
 			}
 		}
 		catch (InterruptedException e){
 			System.out.println(e);
 		}
 		if (score <= 0){
 			System.out.println("[age = " + age( ) + "ms] " + name + " ==> I don't have any points to wager, goodbye");
 			synchronized(game.waitForFinalAnswer){
 				game.waitForFinalAnswer.notify();
 			}
 			return;
 		}
 		wager = r.nextInt(score) + 1;
 		System.out.println("[age = " + age( ) + "ms] " + name + " ==> I wager " + wager + " of my " + score + " points");
 		synchronized(game.waitForFinalAnswer){
 			game.waitForFinalAnswer.notify();
 		}
 	}


    // Announcer part

 	public void announcer(){
    	try{
    		startTime = System.currentTimeMillis( );
    		String function = "initalized";
    		while (!(function.equals("Done"))){
    			function = in.readUTF();
    			if (function.equals("startExam")){
    				startExam();
    				out.writeUTF("complete");
    			}
    			if (function.equals("gradeExams")){
    				gradeExams();
    				out.writeUTF("complete");
    			}
    			if (function.equals("startTheShow")){
    				startTheShow();
    				out.writeUTF("complete");
    			}
    		}
    	}
    	catch (Exception e) {
            e.printStackTrace();
        }
    }

	// announcer waits for everyone to finish grouping and be ready to sit, once they are he notifies the groups
	// using the game.groups vector to sit and they take their exams
	public void startExam(){
		System.out.println("\n" + "[age = " + age( ) + "ms] Announcer   ====> Once everyone is seated, the exam may begin " + "\n");
		if (game.numWaitingToSit != game.num_contestants){
			try{
				synchronized(game.waitForAllReadyToSit){
					game.waitForAllReadyToSit.wait();
				}
			}
			catch (InterruptedException e){
				System.out.println(e);
			}
		}
		for (int i = 0; i < game.groups.size(); i++){
			synchronized(game.groups.elementAt(i)){
				game.groups.elementAt(i).notifyAll();
			}
		}
		if (game.numSitting != game.num_contestants){
			try{
				synchronized(game.waitForAllSitting){
					game.waitForAllSitting.wait();
				}
			}
			catch (InterruptedException e){
				System.out.println(e);
			}
		}
		synchronized(game.sittingContestants){
			game.sittingContestants.notifyAll();
		}
	}

	// announcer grades the exams and notifies the contestents in FCFS order using the game.contestants vector if they are a winner or not
	public void gradeExams(){
		if (game.numFinishedExam != game.num_contestants){
			try{
				synchronized(game.waitForAllFinishedExam){
					game.waitForAllFinishedExam.wait();
				}
			}
			catch (InterruptedException e){
				System.out.println(e);
			}
		}
		System.out.println("\n" + "[age = " + age( ) + "ms] Announcer   ====> Exam is over, grading the exams" + "\n");
		int[] answers = new int[game.num_contestants];
		for (int i = 0; i < game.num_contestants; i++){
			answers[i] = r.nextInt();
		}
		int[] sortedAnswers = answers.clone();
		Arrays.sort(sortedAnswers);
		for (int i = 0; i < game.num_contestants; i++){
			if ((answers[i] == sortedAnswers[game.num_contestants - 1]) ||
				(answers[i] == sortedAnswers[game.num_contestants - 2]) ||
				(answers[i] == sortedAnswers[game.num_contestants - 3]) ||
				(answers[i] == sortedAnswers[game.num_contestants - 4]))
			{
				synchronized(game.contestants.elementAt(i)){
					game.contestants.elementAt(i).isWinner = true;
					System.out.println("[age = " + age( ) + "ms] " + game.contestants.elementAt(i).name + " ==> Is a winner!");
					game.contestants.elementAt(i).notify();
				}
			}
			else{
				synchronized(game.contestants.elementAt(i)){
					System.out.println("[age = " + age( ) + "ms] " + game.contestants.elementAt(i).name + " ==> Is a loser");
					game.contestants.elementAt(i).notify();
				}
			}
		}
	}

	// announcer introduces the contestants and tells the host to start the game, he then exits
	public void startTheShow(){
		System.out.println("\n" + "[age = " + age( ) + "ms] Announcer   ====> Welcome to Guess What or Who!!!!!!!!1!!!");
		//new Host(game).start();
		System.out.println("[age = " + age( ) + "ms] Announcer   ====> Introducing our contestants: ");
		for (int i = 0; i < game.num_contestants; i++){
			if(game.contestants.elementAt(i).isWinner){
				System.out.println("[age = " + age( ) + "ms] Announcer   ====> " + game.contestants.elementAt(i).name);
				game.gameContestants.addElement(game.contestants.elementAt(i));
			}
		}
		System.out.println();
		synchronized(game.waitForAnnouncer){
			game.waitForAnnouncer.notifyAll();
		}
		try {
			synchronized(game.waitToStartGame){
				game.waitToStartGame.wait();
			}
		}
		catch (InterruptedException e){
			System.out.println(e);
		}
		synchronized(game.waitToStartShow){
			game.waitToStartShow.notify();
		}
		System.out.println("\n[age = " + age( ) + "ms] Announcer   ====> Thats it for me, i'm exiting, goodbye\n");
	}


 	// Host Part

    public void host(){
    	try{
    		startTime = System.currentTimeMillis( );
    		String function = "initalized";
    		while (!(function.equals("Done"))){
    			function = in.readUTF();
    			if (function.equals("startTheGameHost")){
    				startTheGameHost();
    				out.writeUTF("complete");
    			}
    			if (function.equals("playTheGameHost")){
    				playTheGameHost();
    				out.writeUTF("complete");
    			}
    			if (function.equals("playFinalHost")){
    				playFinalHost();
    				out.writeUTF("complete");
    			}
    		}
    	}
    	catch (Exception e) {
            e.printStackTrace();
        }
    }

	// host lets the contestants know that the game is starting
	public void startTheGameHost(){
		try{
			synchronized(game.waitToStartShow){
				game.waitToStartShow.wait();
			}
		}
		catch (InterruptedException e){
			System.out.println(e);
		}
		System.out.println("[age = " + age( ) + "ms] Host   ====> Lets play Guess What or Who\n");
		synchronized(game.waitForHost){
			game.waitForHost.notifyAll();
		}
	}

	// host asks questions for numRounds rounds and numQuestions questions, he keeps track of scores as the game plays
	// contestants get the rightPercent percentage of questions correct, they are awarded questionValues points for a correct answer
	public void playTheGameHost(){
		for(int i = 0; i < game.numRounds; i++){
			System.out.println("[age = " + age( ) + "ms] Host   ====> Starting round #" + (i+1) + "\n");
			for(int j = 0; j < game.numQuestions; j++){
				System.out.println("[age = " + age( ) + "ms] Host   ====> Asking round #" + (i+1) + " question #" + (j+1));
				if (game.numFinishedThinking != 4){
					try{
						synchronized(game.waitForAnswer){
							game.waitForAnswer.wait();
						}
					}
					catch (InterruptedException e){
						System.out.println(e);
					}
				}
				for (int k = 0; k < game.gameContestants.size(); k++){
					if (game.gameContestants.elementAt(k).first){

						int answer = r.nextInt(100)+1;
						if (answer > (int)(game.rightPercent*100)){
							System.out.println("[age = " + age( ) + "ms] Host   ====> " + game.gameContestants.elementAt(k).name
														 + " answers the question incorrectly");
							game.gameContestants.elementAt(k).score -= game.questionValues;
							System.out.println("[age = " + age( ) + "ms] Host   ====> " + game.gameContestants.elementAt(k).name
														 + " has score = " + game.gameContestants.elementAt(k).score + "\n");
						}
						else{
							System.out.println("[age = " + age( ) + "ms] Host   ====> " + game.gameContestants.elementAt(k).name
														 + " answers the question correctly");
							game.gameContestants.elementAt(k).score += game.questionValues;
							System.out.println("[age = " + age( ) + "ms] Host   ====> " + game.gameContestants.elementAt(k).name
														 + " has score = " + game.gameContestants.elementAt(k).score + "\n");
						}
						game.gameContestants.elementAt(k).first = false;
						game.answered = false;
						game.numFinishedThinking = 0;
						synchronized(game.waitForNextRound){
							game.waitForNextRound.notifyAll();
						}
					}
				}
			}
		}
		System.out.println("[age = " + age( ) + "ms] Host   ====> All rounds complete");
		System.out.println("[age = " + age( ) + "ms] Host   ====> Lets see our contestants scores:");
		for (int i = 0; i < game.gameContestants.size(); i++){
			System.out.println("[age = " + age( ) + "ms] Host   ====> " + game.gameContestants.elementAt(i).name
					 + " has score = " + game.gameContestants.elementAt(i).score);
		}
		System.out.println();
	}

	// host notifies the contestants in FCFS order using the game.gameContestants vector and they play final Guess What or Who
	// after all contestants have played the host shows the final scores, announces the winner, and ends the game with a friendly message
	public void playFinalHost(){
		if (game.numWaitingToStartFinal != 4){
			try{
				synchronized(game.waitToStartFinal){
					game.waitToStartFinal.wait();
				}
			}
			catch (InterruptedException e){
				System.out.println(e);
			}
		}
		System.out.println("[age = " + age( ) + "ms] Host   ====> Time for final Guess What or Who\n");
		for (int i = 0; i < game.gameContestants.size(); i++){
			synchronized(game.gameContestants.elementAt(i)){
				game.gameContestants.elementAt(i).notify();

			}
			try{
				synchronized(game.waitForFinalAnswer){
					game.waitForFinalAnswer.wait();
				}
			}
			catch (InterruptedException e){
				System.out.println(e);
			}

			if (game.gameContestants.elementAt(i).score > 0){
				int answer = r.nextInt(100)+1;
				if (answer > 50){

					System.out.println("[age = " + age( ) + "ms] Host   ====> " + game.gameContestants.elementAt(i).name
							 + " answered the final question correctly\n");
					game.gameContestants.elementAt(i).score += game.gameContestants.elementAt(i).wager;
				}
				else{
					System.out.println("[age = " + age( ) + "ms] Host   ====> " + game.gameContestants.elementAt(i).name
							 + " answered the final question incorrectly\n");
					game.gameContestants.elementAt(i).score -= game.gameContestants.elementAt(i).wager;
				}
			}
			else{
				System.out.println();
			}

		}
		System.out.println("[age = " + age( ) + "ms] Host   ====> The final round is over");
		System.out.println("[age = " + age( ) + "ms] Host   ====> Lets take a look at the final results:");
		int topScore = -20000;
		for (int i = 0; i < game.gameContestants.size(); i++){
			if (game.gameContestants.elementAt(i).score > topScore){
				topScore = game.gameContestants.elementAt(i).score;
			}
			System.out.println("[age = " + age( ) + "ms] Host   ====> " + game.gameContestants.elementAt(i).name
					 + " has a final score of: " + game.gameContestants.elementAt(i).score + " points");
		}
		for (int i = 0; i < game.gameContestants.size(); i++){
			if (topScore == game.gameContestants.elementAt(i).score){
				System.out.println("\n[age = " + age( ) + "ms] Host   ====> The winner is " + game.gameContestants.elementAt(i).name
						+ " with a score of: " + game.gameContestants.elementAt(i).score + " points\n");
			}
		}
		System.out.println("[age = " + age( ) + "ms] Host   ====> That concludes this game of Guess What or Who");
		System.out.println("[age = " + age( ) + "ms] Host   ====> Thank you for playing, goodbye");
	}
}
