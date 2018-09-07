import java.util.ArrayList;

public class Board {
	public static final int BLACK = 1;
	public static final int WHITE = -1;
	public static final int NONE = 0;
	public static final int WIN = 1;
	public static final int LOSE = -1;
	public static final int DRAW = 0;
	public static final String[] STONE = {"○", "　", "●"};

	private int[][] board;

	public Board() {
		// ゲームボードの初期化
		board = new int[8][8];
		for (int i=0; i<8; i++) {
			for (int j=0; j<8; j++) {
				board[i][j] = Board.NONE;
			}
		}

		board[3][3] = board[4][4] = Board.BLACK;
		board[3][4] = board[4][3] = Board.WHITE;
	}

	public ArrayList<Position> getPutList(int turn) {
		ArrayList<Position> putList = new ArrayList<>();

		for (int i=0; i<8; i++) {
			for (int j=0; j<8; j++) {
				// 石があるところは置けないので何もしない
				if (board[i][j] != Board.NONE) {
					continue;
				}

				// 各マスの周囲8マスを調べる
				// （端を考慮する）
				int minXDelta = j-1 < 0 ? 0 : -1;
				int maxXDelta = j+1 >= 8 ? 0 : 1;
				int minYDelta = i-1 < 0 ? 0 : -1;
				int maxYDelta = i+1 >= 8 ? 0 : 1;

				boolean flag = false;

				for (int yDelta=minYDelta; yDelta<=maxYDelta; yDelta++) {
					int y = i + yDelta;
					for (int xDelta=minXDelta; xDelta<=maxXDelta; xDelta++) {
						int x = j + xDelta;

						// 隣接するマスが相手の駒でなければ何もしない
						if (board[y][x] != turn*(-1)) {
							continue;
						}

						// 相手の駒がある方向を相手の駒が続くまで調べる
						int tmpX = x + xDelta;
						int tmpY = y + yDelta;
						while(tmpX >=0 && tmpX < 8 && tmpY >= 0 && tmpY < 8 &&
							board[tmpY][tmpX] == turn*(-1)) {
							tmpX += xDelta;
							tmpY += yDelta;
						}

						// 続いた先が自分の駒であれば置ける判定を立てる
						if (tmpX >= 0 && tmpX < 8 && tmpY >= 0 && tmpY < 8 &&
							board[tmpY][tmpX] == turn) {
							putList.add(new Position(j, i));
							flag = true;
							break;
						}
					}

					// 置ける判定が立っていたらループを抜ける
					if (flag) { 
						break;
					}
				}
			}
		}

		return putList;
	}

	public void putStone(Position position, int turn) {
		// 指定した場所にコマを置く
		board[position.getY()][position.getX()] = turn;

		// コマをひっくり返すために周囲8マスを調べる
		int minXDelta = position.getX()-1 < 0 ? 0 : -1; 
		int maxXDelta = position.getX()+1 >= 8 ? 0 : 1; 
		int minYDelta = position.getY()-1 < 0 ? 0 : -1; 
		int maxYDelta = position.getY()+1 >= 8 ? 0 : 1;

		for (int iDelta=minYDelta; iDelta<=maxYDelta; iDelta++) {
			int i = position.getY() + iDelta;
			for (int jDelta=minXDelta; jDelta<=maxXDelta; jDelta++) {
				int j = position.getX() + jDelta;

				// 隣接するコマが相手の駒じゃなければ何もしない
				if (board[i][j] != turn*(-1)) {
					continue;
				}

				// ひっくり返す可能性のあるコマを格納するリストを作成
				ArrayList<Position> reversePositions = new ArrayList<>();
				reversePositions.add(new Position(j, i));

				// 相手の駒がある方向を相手の駒が続くまで調べる
				int tmpX = j + jDelta;
				int tmpY = i + iDelta;
				while (tmpX >= 0 && tmpX < 8 && tmpY >= 0 && tmpY < 8 &&
					board[tmpY][tmpX] == turn*(-1)) {
					reversePositions.add(new Position(tmpX, tmpY));
					tmpX += jDelta;
					tmpY += iDelta;
				}

				// 相手の駒が続く先が自分の駒であれば間のコマをひっくり返す。
				if (tmpX >= 0 && tmpX < 8 && tmpY >= 0 && tmpY < 8 &&
					board[tmpY][tmpX] == turn) {
					for (Position p : reversePositions) {
						board[p.getY()][p.getX()] = turn;
					}
				}
			}
		}
	}

	public boolean isFinished() {
		// 白駒、空きコマ、黒駒の数を数える
		int[] counts = { 0, 0, 0 };
		for (int i=0; i<8; i++) {
			for (int j=0; j<8; j++) {
				counts[board[i][j]+1]++;
			}
		}

		// どれか駒が0個もしくは空きコマが0個であれば終了とする
		return counts[0] == 0 || counts[1] == 0 || counts[2] == 0;
	}

	public int[][] copy() {
		int[][] copyBoard = new int[8][8];
		for (int i=0; i<8; i++) {
			for (int j=0; j<8; j++) {
				copyBoard[i][j] = board[i][j];
			}
		}

		return copyBoard;
	}

	public int[] getResult() {
		// 白駒、空きコマ、黒駒の数を数える
		int[] counts = { 0, 0, 0 };
		for (int i=0; i<8; i++) {
			for (int j=0; j<8; j++) {
				counts[(board[i][j] + 1)]++;
			}
	 	}

		return counts;
	}

	public int getState(int x, int y) {
		return board[y][x];
	}

	public String toString() {
		String str = "+--+--+--+--+--+--+--+--+\n";
		int[] counts = new int[3];

		for (int i=0; i<8; i++) {
			str += "|";
			for (int j=0; j<8; j++) {
				counts[(board[i][j] + 1)]++;
				str += Board.STONE[board[i][j] + 1] + "|";
			}
			str += "\n+--+--+--+--+--+--+--+--+\n";
		}

		str += "●" + counts[2] + "\n";
		str += "○" + counts[0] + "\n";

		return str;
	}
}
