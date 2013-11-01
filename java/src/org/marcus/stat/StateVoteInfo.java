package org.marcus.stat;

import java.text.DecimalFormat;

public class StateVoteInfo {

	private final States state;
	private final boolean senate;
	private final int houseSeats;
	private int senateGOPVotes = 0;
	private int senateDemVotes = 0;
	private int senateFstVotes = 0;
	private int senateDemWins = 0;
	private int houseGOPVotes = 0;
	private int houseDemVotes = 0;
	private int houseFstVotes = 0;
	private int houseDemWins = 0;

	public StateVoteInfo(String stateLetters, boolean senateRace,
			int seatsInHouse) {
		state = States.valueOf(stateLetters);
		senate = senateRace;
		houseSeats = seatsInHouse;
	}

	public StateVoteInfo(String s) {
		state = States.valueOf(s.substring(0, 2));
		if (s.charAt(2) == 5) {
			senate = true;
		} else {
			senate = false;
		}
		houseSeats = Integer.parseInt(s.substring(3, 5));
	}

	public int hashCode() {
		return state.ordinal();
	}

	public String toString() {
		String s = (String.format("%02d", houseSeats)) + "0";
		if (senate) {
			s = s.substring(0, 2) + "1";
			DecimalFormat f = new DecimalFormat("00000000");
			s = s + f.format(senateDemVotes) + f.format(senateGOPVotes)
					+ f.format(houseDemVotes) + f.format(houseGOPVotes);
		} else {
			DecimalFormat f = new DecimalFormat("00000000");
			s = s + f.format(houseDemVotes) + f.format(houseGOPVotes);
		}
		return state.toString() + s;
	}

	public static StateVoteInfo parseInfo(String s) {
		return new StateVoteInfo(s);
	}

	public boolean addVotes(String seat, String party, int votes) {
		if (seat.contains("Senate")) {
			if (party.contains("Dem")) {
				senateDemVotes = senateDemVotes + votes;
			} else if (party.contains("GOP")) {
				senateGOPVotes = senateGOPVotes + votes;
			} else {
				return false;
			}
		} else if (seat.contains("House")) {
			if (party.contains("Dem")) {
				houseDemVotes = houseDemVotes + votes;
			} else if (party.contains("GOP")) {
				houseGOPVotes = houseGOPVotes + votes;
			} else {
				return false;
			}
		} else {
			return false;
		}
		return true;
	}

	public boolean setFstVotes(String seat, int votes) {
		if (seat.contains("Senate")) {
			senateFstVotes = votes;
		} else if (seat.contains("House")) {
			houseFstVotes = votes;
		} else {
			return false;
		}
		return true;
	}

	public boolean setWins(String seat, int wins) {
		if (seat.contains("Senate")) {
			senateDemWins = wins;
		} else if (seat.contains("House")) {
			houseDemWins = wins;
		} else {
			return false;
		}
		return true;
	}

	public String state() {
		return state.toString();
	}

	public boolean isSenate() {
		return senate;
	}

	public int houseSeats() {
		return houseSeats;
	}

	public double senateRatio() {
		if (senateGOPVotes == senateFstVotes) {
			return (double) senateDemVotes
					/ (double) (senateGOPVotes + senateDemVotes);
		} else if (senateDemVotes == senateFstVotes) {
			return (double) senateDemVotes
					/ (double) (senateGOPVotes + senateDemVotes);
		} else if (senateDemVotes > senateGOPVotes) {
			return (double) senateDemVotes
					/ (double) (senateFstVotes + senateDemVotes);
		} else {
			return (double) senateDemVotes
					/ (double) (senateGOPVotes + senateFstVotes);
		}
	}

	public int senateVotes() {
		return (senateDemVotes + senateGOPVotes);
	}

	public int senateWins() {
		return senateDemWins;
	}

	public int senateFstVotes() {
		return senateFstVotes;
	}

	public double houseRatio() {
		if (houseGOPVotes == houseFstVotes) {
			return (double) houseDemVotes
					/ (double) (houseGOPVotes + houseDemVotes);
		} else if (houseDemVotes == houseFstVotes) {
			return (double) houseDemVotes
					/ (double) (houseGOPVotes + houseDemVotes);
		} else if (houseDemVotes > houseGOPVotes) {
			return (double) houseDemVotes
					/ (double) (houseFstVotes + houseDemVotes);
		} else {
			return (double) houseDemVotes
					/ (double) (houseGOPVotes + houseFstVotes);
		}
	}

	public int houseVotes() {
		return (houseDemVotes + houseGOPVotes);
	}

	public int houseWins() {
		return houseDemWins;
	}

	public int houseFstVotes() {
		return houseFstVotes;
	}

}
