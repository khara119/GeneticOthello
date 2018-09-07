import java.util.ArrayList;
import java.util.Random;

public class Player {
	private static final double MUTATION_RATE = 0.01;
	private String name;
	private RecordBoard recordBoard;
	private ArrayList<Position> putList;
	private int score;

	public Player(String name) {
		this.name = name;
		recordBoard = new RecordBoard();
		putList = new ArrayList<>();
		score = 0;
	}

	public Player(Player parent1, Player parent2) {
		this.name = parent1.getName();
		recordBoard = new RecordBoard(
			parent1.getRecordBoard(),
			parent2.getRecordBoard()
		);
		putList = new ArrayList<>();
		score = 0;
	}

	public Position play(ArrayList<Position> putPosition) {
		// 置ける場所がなければNULLを返す
		if (putPosition.isEmpty()) {
			return null;
		}

		// 置ける場所から一番優先する場所を選択する
		Position p = recordBoard.getMaxPutPosition(putPosition);
		putList.add(p);
		return p;
	}

	public void updateRecordBoard(Board board, int myStone) {
		this.recordBoard.update(board, myStone);
	}

	public void updateResult(int[] states) {
		// 自身のコマ数と相手のコマ数、空白マスによってスコアを加算する
		score += (states[0] - states[2]) * (states[1] * 2);

		//recordBoard.update(putList, states);
		putList.clear();
	}

	public void mutation() {
		Random random = new Random();
		for (int i=0; i<8; i++) {
			for (int j=0; j<8; j++) {
				// ランダムで2箇所の優先度を交換する
				if (random.nextDouble() < Player.MUTATION_RATE) {
					int x = random.nextInt(8);
					int y = random.nextInt(8);
					recordBoard.mutation(j, i, x, y);
				}
			}
		}
	}

	public String getName() {
		return this.name;
	}

	public RecordBoard getRecordBoard() {
		return recordBoard;
	}

	public int getScore() {
		return this.score;
	}
}
