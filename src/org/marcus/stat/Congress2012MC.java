package org.marcus.stat;

import java.security.interfaces.RSAKey;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Congress2012MC {

	private static String distinctState = "SELECT DISTINCT [State] FROM [USCongress].[dbo].[US-congress-race-2012seats] ORDER BY [State] ASC";
	private static String distinctSeatPerState = "SELECT DISTINCT [OfficeName] FROM [USCongress].[dbo].[US-congress-race-2012seats] ORDER BY [OfficeName] ASC";
	private static String seatsPerState = "SELECT COUNT(*) FROM [USCongress].[dbo].[US-congress-race-2012seats] WHERE [OfficeName] = ? AND [State] = ?";
	private static String votePerSeatPartyState = "SELECT SUM([cv].[Votes]) FROM [USCongress].[dbo].[US-congress-race-2012seats] as [seat] JOIN [USCongress].[dbo].[CorrectedVotes] as [cv] ON [seat].[NPID] = [cv].[NPID] WHERE [seat].[NPID] = [cv].[NPID] AND [cv].[Party] = ? AND [seat].[OfficeName] = ? AND [cv].[State] = ?";
	private static String distinctParty = "SELECT DISTINCT [Party] FROM [USCongress].[dbo].[US-congress-race-2012seats] AS [seat] JOIN [USCongress].[dbo].[CorrectedVotes] AS [cv] ON [seat].[NPID] = [cv].[NPID] WHERE ([Party] = 'Dem' OR [Party] = 'GOP') AND [OfficeName] = ? AND [cv].[State] = ?";

	public static void main(String[] args) throws ClassNotFoundException,
			SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		String connectionURL = "jdbc:sqlserver://FRENUM\\SQLEXPRESS;integratedSecurity=true;databaseName=USCongress;";
		Connection con = DriverManager.getConnection(connectionURL);
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(distinctState);

		StateVoteInfo[] cat = new StateVoteInfo[50];

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

		// iterate over states
		while (rs.next()) {
			stateNum = stateNum + 1;
			String state = rs.getString(1);
			// note if there was a senator elected
			boolean hasSen = false;
			// find number of house seats
			int nHouseSeats;
			{
				// reuse vars seats and seatsrs
				seats = con.prepareStatement(seatsPerState);
				seats.setString(1, seatName[0]);
				seats.setString(2, state);
				seatsrs = seats.executeQuery();
				seatsrs.next();
				nHouseSeats = seatsrs.getInt(1);
				seats.setString(1, seatName[1]);
				seatsrs = seats.executeQuery();
				seatsrs.next();
				if (seatsrs.getInt(1) > 0)
					hasSen = true;
			}
			// initialize StateVoteInfo array entry
			cat[stateNum] = new StateVoteInfo(state, hasSen, nHouseSeats);
			for (int i = 0; i < seatName.length; i++) {
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
					cat[stateNum].addVotes(seatName[i], party, votesrs.getInt(1));
				}
				if (!hasSen)
					break;
			}
		}
		
		for (int i=0;i<cat.length;i++){
			System.out.println(cat[i]);
		}
	}
}