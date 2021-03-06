package org.marcus.stat;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Random;

public class Congress2012MC {

	private static String distinctState = "SELECT DISTINCT [State] FROM [USCongress].[dbo].[US-congress-race-2012seats] ORDER BY [State] ASC";
	private static String distinctSeatPerState = "SELECT DISTINCT [OfficeName] FROM [USCongress].[dbo].[US-congress-race-2012seats] ORDER BY [OfficeName] ASC";
	private static String seatsPerState = "SELECT COUNT(*) FROM [USCongress].[dbo].[US-congress-race-2012seats] WHERE [OfficeName] = ? AND [State] = ?";
	private static String votePerSeatPartyState = "SELECT SUM([cv].[Votes]) FROM [USCongress].[dbo].[US-congress-race-2012seats] as [seat] JOIN [USCongress].[dbo].[CorrectedVotes] as [cv] ON [seat].[NPID] = [cv].[NPID] WHERE [seat].[NPID] = [cv].[NPID] AND [cv].[Party] = ? AND [seat].[OfficeName] = ? AND [cv].[State] = ?";
	private static String distinctParty = "SELECT DISTINCT [Party] FROM [USCongress].[dbo].[US-congress-race-2012seats] AS [seat] JOIN [USCongress].[dbo].[CorrectedVotes] AS [cv] ON [seat].[NPID] = [cv].[NPID] WHERE ([Party] = 'Dem' OR [Party] = 'GOP') AND [OfficeName] = ? AND [cv].[State] = ?";
	private static String winsPerPartyState = "SELECT COUNT(*) FROM [USCongress].[dbo].[CorrectedVotes] [cv], [USCongress].[dbo].[US-congress-race-2012seats] AS [seat] WHERE [cv].[NPID] = [seat].[NPID] AND [cv].[Winner] = 1 AND [Party] = 'Dem' AND [seat].[OfficeName] = ? AND [cv].[State] = ?";
	private static String winsPerOffice = "SELECT COUNT(*) FROM [USCongress].[dbo].[CorrectedVotes] AS [cv], [USCongress].[dbo].[US-congress-race-2012seats] AS [seat] WHERE [cv].[NPID] = [seat].[NPID] AND [cv].[Party] = 'Dem' AND [cv].[Winner] = 1 AND [seat].[OfficeName] = ?";
	private static String totalSeats = "SELECT COUNT(*), [OfficeName] FROM [USCongress].[dbo].[US-congress-race-2012seats] GROUP BY [OfficeName] ORDER BY [OfficeName] ASC";
	private static String secndVotesPerSS = "SELECT SUM([cv].[Votes]) AS [Votes], [cv].[Party] FROM [USCongress].[dbo].[US-congress-race-2012seats] as [seat], [USCongress].[dbo].[CorrectedVotes] as [cv] WHERE [seat].[NPID] = [cv].[NPID] AND [seat].[OfficeName] = ? AND [seat].[NPID] = [cv].[NPID] AND [cv].[State] = ? AND [cv].[Party] != 'Dem' GROUP BY [cv].[Party]";
	private static double relerr = 0.06;

	public static void main(String[] args) throws ClassNotFoundException,
			SQLException, IOException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		String connectionURL = "jdbc:sqlserver://FRENUM\\SQLEXPRESS;integratedSecurity=true;databaseName=USCongress;";
		Connection con = DriverManager.getConnection(connectionURL);
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(distinctState);

		StateVoteInfo[] cat = new StateVoteInfo[50];
		int totHouseWins = 0;
		int totSenateWins = 0;
		int[] nTotSeats = new int[2];

		int stateNum = -1;

		// make array of seat names
		PreparedStatement seats = con.prepareStatement(distinctSeatPerState);
		ResultSet seatsrs = seats.executeQuery();
		String[] seatName = new String[2];
		seatsrs.next();
		seatName[0] = seatsrs.getString(1);
		seatsrs.next();
		seatName[1] = seatsrs.getString(1);
		seatsrs.close();
		seats.close();

		seats = con.prepareStatement(totalSeats);
		seatsrs = seats.executeQuery();
		seatsrs.next();
		nTotSeats[0] = seatsrs.getInt(1);
		seatsrs.next();
		nTotSeats[1] = seatsrs.getInt(1);
		seatsrs.close();
		seats.close();

		// iterate over states
		while (rs.next()) {
			stateNum = stateNum + 1;
			String state = rs.getString(1);
			// note if there was a senator elected
			boolean hasSen = false;
			// find number of house seats
			int nHouseSeatsbyState;
			{
				// reuse vars seats and seatsrs
				seats = con.prepareStatement(seatsPerState);
				seats.setString(1, seatName[0]);
				seats.setString(2, state);
				seatsrs = seats.executeQuery();
				seatsrs.next();
				nHouseSeatsbyState = seatsrs.getInt(1);
				seats.setString(1, seatName[1]);
				seatsrs = seats.executeQuery();
				seatsrs.next();
				if (seatsrs.getInt(1) > 0)
					hasSen = true;
				seatsrs.close();
				seats.close();
			}
			// initialize StateVoteInfo array entry
			cat[stateNum] = new StateVoteInfo(state, hasSen, nHouseSeatsbyState);
			for (int i = 0; i < seatName.length; i++) {
				// find and set the number of Dem wins
				{
					// reuse vars seats and seatsrs
					seats = con.prepareStatement(winsPerPartyState);
					seats.setString(1, seatName[i]);
					seats.setString(2, state);
					seatsrs = seats.executeQuery();
					seatsrs.next();
					cat[stateNum].setWins(seatName[i], seatsrs.getInt(1));
					seatsrs.close();
					seats.close();
				}
				// find the number of party wins per seat
				{
					seats = con.prepareStatement(winsPerOffice);
					seats.setString(1, seatName[0]);
					seatsrs = seats.executeQuery();
					seatsrs.next();
					totHouseWins = Integer.parseInt(seatsrs.getString(1));
					seats.setString(1, seatName[1]);
					seatsrs = seats.executeQuery();
					seatsrs.next();
					totSenateWins = Integer.parseInt(seatsrs.getString(1));
				}

				PreparedStatement parties = con.prepareStatement(distinctParty);
				// set the office name (1) and state (2)
				parties.setString(1, seatName[i]);
				parties.setString(2, state);
				ResultSet partyrs = parties.executeQuery();
				// iterate over parties
				while (partyrs.next()) {
					String party = partyrs.getString(1);
					PreparedStatement votes = con
							.prepareStatement(votePerSeatPartyState);
					// set party (1), office name (2), state (3)
					votes.setString(1, party);
					votes.setString(2, seatName[i]);
					votes.setString(3, state);
					ResultSet votesrs = votes.executeQuery();
					votesrs.next();
					// record number of votes
					cat[stateNum].addVotes(seatName[i], party,
							votesrs.getInt(1));
				}
				PreparedStatement votes = con.prepareStatement(secndVotesPerSS);
				votes.setString(1, seatName[i]);
				votes.setString(2, state);
				ResultSet votesrs = votes.executeQuery();
				votesrs.next();
				cat[stateNum].setFstVotes(seatName[i], votesrs.getInt(1));
				votes.close();
				votesrs.close();

				parties.close();
				partyrs.close();
				if (!hasSen)
					break;
			}
		}

		// for (int i = 0; i < cat.length; i++) {
		// double hr = cat[i].houseRatio();
		// int houseWins = cat[i].houseWins();
		// int houseSeats = cat[i].houseSeats();
		// if (cat[i].isSenate()) {
		// double sr = cat[i].senateRatio();
		// System.out.println((int) Math.round(randomSim(hr,
		// cat[i].houseVotes())
		// * (double) houseSeats)
		// + " "
		// + houseWins
		// + "/"
		// + houseSeats
		// + " "
		// + (int) Math.round(randomSim(sr, cat[i].senateVotes()))
		// + " " + (int) cat[i].senateWins() + "/1");
		// } else {
		// System.out.println((int) Math.round(randomSim(hr,
		// cat[i].houseVotes())
		// * (double) houseSeats)
		// + " " + houseWins + "/" + houseSeats);
		// }
		// }
		runSim(cat, Integer.parseInt(args[0]), totHouseWins, totSenateWins,
				nTotSeats);
		rs.close();
	}

	private static double randomSim(double ratio, long n) {
		long seed = Calendar.getInstance().getTimeInMillis() % 234185981;
		Random rand = new Random(seed);
		long pos = 0;
		for (int i = 0; i < n; i++) {
			double err = relerr * (rand.nextDouble() - 0.5);
			double r = rand.nextDouble()
					+ err;
//			System.out.println(err);
			// System.out.println(r);
			if (r <= ratio)
				pos++;
		}
		return (double) pos / (double) n;
	}

	private static void runSim(StateVoteInfo[] cat, int n, int totHouseWins,
			int totSenateWins, int[] nTotSeats) throws IOException {
		FileWriter fwo = new FileWriter(Calendar.getInstance()
				.getTimeInMillis() + "output.dat");
		BufferedWriter bwo = new BufferedWriter(fwo);
		FileWriter fws = new FileWriter(Calendar.getInstance()
				.getTimeInMillis() + "summary.dat");
		BufferedWriter bws = new BufferedWriter(fws);
		long time = System.currentTimeMillis();
		// total wins for all of the states each
		int[] yearTot = new int[2];
		// total wins for all of the simulations
		int[] tot = new int[2];
		// simulations with more wins
		int[] moreWins = new int[2];
		// simulations where dems got a majority
		int[] majority = new int[2];
		DecimalFormat f = new DecimalFormat("00");
		for (int year = 0; year < n; year++) {
			System.out.println("Year " + String.format("%06d", year));
			// iterate over states
			for (int i = 0; i < cat.length; i++) {
				int wins = 0;
				StateVoteInfo q = cat[i];
				String s;
				s = String.format("%06d", year) + q.state();
				s = s + f.format(q.houseSeats()) + "0";
				if (q.isSenate()) {
					s = s.substring(0, s.length() - 1) + "1";
					// number of senate wins for this state
					double randomSim = randomSim(q.senateRatio(),
							q.senateVotes() / 100);
//					System.out.println(randomSim+" "+q.senateRatio());
					wins = (int) Math.round(randomSim);
					s = s + f.format(wins);
//					if (wins == 0)
//						System.out.println(q.state());
					// set statetot
					yearTot[1] = yearTot[1] + wins;
				}
				// number of house wins for this state
				wins = (int) Math.round(randomSim(q.houseRatio(),
						q.houseVotes() / 100)
						* q.houseSeats());
				s = s + f.format(wins);
				// set statetot
				yearTot[0] = yearTot[0] + wins;

				bwo.write(s);
				bwo.newLine();
			}
			// increment moreWins if yearTot greater than reality
			if (yearTot[0] > totHouseWins)
				moreWins[0]++;
			if (yearTot[1] > totSenateWins)
				moreWins[1]++;
			// increment majority if achieved
			if (yearTot[0] > nTotSeats[0] / 2)
				majority[0]++;
			if (yearTot[1] > nTotSeats[1] / 2)
				majority[1]++;
			// update total all time wins
			tot[0] = tot[0] + yearTot[0];
			tot[1] = tot[1] + yearTot[1];
			System.out.println("houseTot:" + yearTot[0] + "senTot:"
					+ yearTot[1] + "nFewerWins:" + (year - moreWins[0] + 1)
					+ "," + (year - moreWins[1] + 1) + "nMinority"
					+ (year - majority[0] + 1) + "," + (year - majority[0] + 1)
					+ "total" + (year + 1) + "of" + n);
			System.out.println("Time elapsed (s): "
					+ (float) (System.currentTimeMillis() - time) / 1000.);
			bws.write("houseTot:" + yearTot[0] + "senTot:" + yearTot[1]
					+ "nFewerWins:" + (year - moreWins[0] + 1) + ","
					+ (year - moreWins[1] + 1) + "nMinority"
					+ (year - majority[0] + 1) + "," + (year - majority[0] + 1)
					+ "total" + (year + 1) + "of" + n);
			yearTot[0] = 0;
			yearTot[1] = 0;
			bwo.flush();
			fwo.flush();
		}
		System.out.println("houseTot:" + tot[0] + "senTot:" + tot[1]
				+ "nMoreWins:" + moreWins[0] + "," + moreWins[1] + "nMinority"
				+ (n - majority[0]) + "," + (n - majority[0]) + "total" + n);
		bwo.close();
		bws.close();
		fwo.close();
		fws.close();
	}
}