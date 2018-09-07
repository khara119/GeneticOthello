import java.util.Arrays;
import java.util.Random;

public class Main {
	// 標準出力後の停止時間
	public static final int SLEEP_TIME = 500;
	// 突然変異率
	public static final double MUTATION_RATE = 0.01;

	public static void main(String[] args) throws Exception {
		// 先手後手各100体でシミュレート
		Player[] blacks = new Player[100];
		Player[] whites = new Player[100];

		for (int i=0; i<100; i++) {
			blacks[i] = new Player("先手");
			whites[i] = new Player("後手");
		}

		// 世代毎に行う優秀者同士の対戦の結果を保存する配列
		// 白勝数、引き分け、黒勝数の順番
		int[] topBattleResult = { 0, 0, 0 };

		// シミュレートする世代数
		for (int i=0; i<10000; i++) {
			//System.out.println((i+1) + "世代目");
			//Thread.sleep(Main.SLEEP_TIME);

			// 先手の人数分ループ
			for (int j=0; j<blacks.length; j++) {
				//System.out.print("先手" + (j+1) + "人目 vs ");

				// 後手の人数分ループ
				for (int k=0; k<whites.length; k++) {
					//System.out.println("後手" + (k+1) + "人目の対戦を開始します。");

					// ゲームボードの生成
					Board gameBoard = new Board();

					// 対戦するプレイヤーを配列に格納
					Player[] players = { blacks[j], whites[k] };
					int turn = Board.BLACK;

					// 配置データの出力（数値が大きいほど優先して置く）
					/*
					for (Player p : players) {
						System.out.println(p.getName() + "のデータ");
						System.out.println(p.getRecordBoard());
					}
					*/
					//System.out.println(gameBoard);

					// 両方置けない状況を検出するフラグ
					boolean checkFlag = false;

					// オセロの開始
					while (!gameBoard.isFinished()) {
						// 手番プレイヤーを変数に格納
						// 黒番白番は1, -1で表している
						Player player = players[(turn-1)/(-2)];

						// ゲームボードから置けるマス一覧を取得し、
						// プレイヤーはその中から置く場所を決める
						Position p = player.play(gameBoard.getPutList(turn));

						// 置ける場所がなければパスとする
						if (p == null) {
							//System.out.println(player.getName() + "は置ける場所がありません");
							// 相手プレイヤーもパスであればゲームを終える
							if (checkFlag) {
								//System.out.println(gameBoard);
								//System.out.println("両方置けなくなりました。");
								//Thread.sleep(SLEEP_TIME);
								break;
							}

							// 置けなかったフラグを立てる
							checkFlag = true;
						} else {
							// 置ける場所があればコマを置く
							gameBoard.putStone(p, turn);

							// 両プレイヤーの置くデータ情報を更新
							players[0].updateRecordBoard(gameBoard, Board.BLACK);
							players[1].updateRecordBoard(gameBoard, Board.WHITE);

							// 置けなかったフラグを戻す
							checkFlag = false;
						}

						//System.out.println(gameBoard);

						// 手番の交代
						// （黒番白番は1, -1で管理）
						turn *= -1;
					}

					// 対戦結果を取得
					int[] whiteResult = gameBoard.getResult();
					int[] blackResult = {
						whiteResult[2],
						whiteResult[1],
						whiteResult[0]
					};

					// 対戦結果をスコアに反映する
					players[0].updateResult(blackResult);
					players[1].updateResult(whiteResult);
				}
			}

			// 次世代のプレイヤーを格納する配列を生成
			Player[] nextBlackPlayers = new Player[100];
			Player[] nextWhitePlayers = new Player[100];

			for (int j=0; j<100; j++) {
				// トーナメント方式（4人）で上位2人を選択する
				Player[][] selectBlackPlayers = new Player[2][2];
				Player[][] selectWhitePlayers = new Player[2][2];

				Random random = new Random();
				// ランダムにトーナメント参加者を決める
				for (int m=0; m<2; m++) {
					for (int n=0; n<2; n++) {
						selectBlackPlayers[m][n] = blacks[random.nextInt(100)];
						selectWhitePlayers[m][n] = whites[random.nextInt(100)];
					}
				}

				// それぞれスコアの降順でソートする
				// （トーナメントの人数を増やしたときに対応できるようにしておく）
				for (int k=0; k<2; k++) {
					Arrays.sort(selectBlackPlayers[k],
						(a,b) -> b.getScore() - a.getScore());
					Arrays.sort(selectWhitePlayers[k],
						(a,b) -> b.getScore() - a.getScore());
				}

				// トーナメントの勝者2名から次世代のプレイヤーを生成する
				nextBlackPlayers[j] = new Player(
					selectBlackPlayers[0][0],
					selectBlackPlayers[1][0]
				);

				nextWhitePlayers[j] = new Player(
					selectWhitePlayers[0][0],
					selectWhitePlayers[1][0]
				);

				// 突然変異処理
				if (random.nextDouble() < Main.MUTATION_RATE) {
					nextBlackPlayers[j].mutation();
				}

				if (random.nextDouble() < Main.MUTATION_RATE) {
					nextWhitePlayers[j].mutation();
				}
			}

			// その世代の一番成績の良いプレイヤーで対戦を行う
			// （単なる確認用）
			System.out.println("第" + (i+1) + "世代の最優秀プレイヤー同士の対戦を開始します。");
			Thread.sleep(SLEEP_TIME);

			// 成績順にソート
			// （次世代プレイヤー生成時はソートしない方がいい気がする）
			Arrays.sort(blacks, (a, b) -> b.getScore() - a.getScore());
			Arrays.sort(whites, (a, b) -> b.getScore() - a.getScore());

			// ゲームボードの生成
			Board gameBoard = new Board();
			Player[] players = { blacks[0], whites[0] };
			int turn = Board.BLACK;

			// 両プレイヤーの置く情報を出力
			// （数値の大きいところに優先的に置く）
			for (Player p : players) {
				System.out.println(p.getName() + "のデータ");
				System.out.println(p.getRecordBoard());
				Thread.sleep(SLEEP_TIME);
			}
			System.out.println(gameBoard);
			Thread.sleep(SLEEP_TIME);

			// 両方置けない場合を検出するフラグ
			boolean checkFlag = false;

			while (!gameBoard.isFinished()) {
				Player player = players[(turn-1)/(-2)];
				Position p = player.play(gameBoard.getPutList(turn));

				// 置けなければパスとする
				if (p == null) {
					System.out.println(player.getName() + "は置ける場所がありません");

					// 相手プレイヤーも置けなければゲームを終了する
					if (checkFlag) {
						System.out.println(gameBoard);
						System.out.println("両方置けなくなりました。");
						Thread.sleep(SLEEP_TIME);
						break;
					}

					// 置けないフラグを立てる
					checkFlag = true;
				} else {
					// コマを置く
					gameBoard.putStone(p, turn);

					// 置けないフラグを戻す
					checkFlag = false;
				}

				System.out.println(gameBoard);
				Thread.sleep(SLEEP_TIME);

				// 手番の交代
				turn *= -1;
			}

			int[] result = gameBoard.getResult();
			if (result[0] > result[2]) {
				topBattleResult[0]++;
			} else if (result[0] < result[2]) {
				topBattleResult[2]++;
			} else {
				topBattleResult[1]++;
			}

			System.out.println("\n黒勝:引分:白勝\n");
			for (int j=2; j>=0; j--) {
				System.out.print(String.format("%4d ", topBattleResult[j]));
			}
			System.out.println();
			Thread.sleep(SLEEP_TIME);

			// 次世代に交代
			blacks = nextBlackPlayers;
			whites = nextWhitePlayers;
		}
	}
}
