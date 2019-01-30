// Nicolas Stoian

import java.util.*;

public class GameServerObject
{
	// fixed parameter
	protected final int exam_time = 3000;

	// adjustable command line parameters
	protected int numRounds = 2;
	protected int numQuestions = 5;
	protected int questionValues = 200;
	protected double rightPercent = 0.65;
	protected int room_capacity = 4;
	protected int num_contestants = 13;

	// game objects
	protected Vector<GameServerThread> contestants = new Vector<GameServerThread>( );
	protected Vector<GameServerThread> gameContestants = new Vector<GameServerThread>( );
	protected Vector<Object> groups = new Vector<Object>( );
	protected Object sittingContestants = new Object();
	protected Object waitForAllReadyToSit = new Object();
	protected Object waitForAllSitting = new Object();
	protected Object waitForAllFinishedExam = new Object();
	protected Object waitToStartShow = new Object();
	protected Object waitToStartGame = new Object();
	protected Object waitForAnnouncer = new Object();
	protected Object waitForHost = new Object();
	protected Object waitForNextRound = new Object();
	protected Object waitForAnswer = new Object();
	protected Object waitToStartFinal = new Object();
	protected Object waitForFinalAnswer = new Object();

	// game counters
	protected int numInGroup = 0;
	protected int numWaitingToSit = 0;
	protected int numSitting = 0;
	protected int numFinishedExam = 0;
	protected int numWaitingGrade = 0;
	protected int numWaitingToStartGame = 0;
	protected int numFinishedThinking = 0;
	protected int numWaitingToStartFinal = 0;
	protected boolean answered = false;
}
