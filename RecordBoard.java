import java.util.ArrayList;
import java.util.Random;

public class RecordBoard {
	private int[][] board;
	private int[][] prevBoardState;

	public RecordBoard() {
		// 優先度を保存するボードと前回のターンの状態を保存するボードの初期化
		board = new int[8][8];
		prevBoardState = new int[8][8];

		for (int i=0; i<8; i++) {
			for (int j=0; j<8; j++) {
				board[i][j] = 0;
				prevBoardState[i][j] = 0;
			}
		}

		prevBoardState[3][3] = prevBoardState[4][4] = Board.BLACK;
		prevBoardState[3][4] = prevBoardState[4][3] = Board.WHITE;
	}

	public RecordBoard(RecordBoard parent1, RecordBoard parent2) {
		// 優先度を保存するボードと前回のターンの状態を保存するボードの初期化
		board = new int[8][8];
		prevBoardState = new int[8][8];
		Random random = new Random();

		// 優先度を親から継承する
		RecordBoard[] parents = { parent1, parent2 };
		for (int i=0; i<8; i++) {
			for (int j=0; j<8; j++) {
				// ランダムにどちらかの親から値を受け継ぐ
				board[i][j] = parents[random.nextInt(2)].getData(j, i);
				prevBoardState[i][j] = 0;
			}
		}

		prevBoardState[3][3] = prevBoardState[4][4] = Board.BLACK;
		prevBoardState[3][4] = prevBoardState[4][3] = Board.WHITE;
	}

	public void update(Board currentBoard, int myStone) {
		for (int i=0; i<8; i++) {
			for (int j=0; j<8; j++) {
				int prevState = prevBoardState[i][j];
				int currentState = currentBoard.getState(j, i);

				// 何もないところに自分の駒を置いたら加算する
				if (prevState == Board.NONE && currentState == myStone) {
					board[i][j] += 1;
				} else if (prevState == myStone && currentState == myStone * (-1)) {
					// 相手にひっくり返されたら減点する
					board[i][j] -= 2;
				} else if (prevState == myStone * (-1) && currentState == myStone) {
					// 相手の駒をひっくり返したら加算する
					board[i][j] += 1;
				}
			}
		}

		prevBoardState = currentBoard.copy();
	}

	public void mutation(int x1, int y1, int x2, int y2) {
		int tmp = board[y1][x1];
		board[y1][x1] = board[y2][x2];
		board[y2][x2] = tmp;
	}

	public Position getMaxPutPosition(ArrayList<Position> putList) {
		ArrayList<Position> maxPutPositions = new ArrayList<>();
		int maxScore = Integer.MIN_VALUE;

		// 置けるリストから一番高い優先度のマスを探す
		for (Position p : putList) {
			// 優先度を更新
			if (board[p.getY()][p.getX()] > maxScore) {
				maxPutPositions.clear();
				maxPutPositions.add(p);
				maxScore = board[p.getY()][p.getX()];
			} else if (board[p.getY()][p.getX()] == maxScore) {
				// 同じ優先度なら追加
				maxPutPositions.add(p);
			}
		}

		// 一番高い優先度のリストからランダムで1箇所選ぶ
		int size = maxPutPositions.size();
		return maxPutPositions.get(new Random().nextInt(size));
	}

	public int getData(int x, int y) {
		return board[y][x];
	}

	public String toString() {
		String str = "+--------+--------+--------+--------+--------+--------+--------+--------+\n";
		for (int i=0; i<8; i++) {
			str += "|";
			for (int j=0; j<8; j++) {
				str += String.format("%8d", board[i][j]) + "|";
			}

			str += "\n+--------+--------+--------+--------+--------+--------+--------+--------+\n";
		}

		return str;
	}
}
