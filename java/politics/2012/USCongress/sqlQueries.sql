-- votes per seat party state

SELECT SUM([cv].[Votes]) FROM [USCongress].[dbo].[US-congress-race-2012seats] as [seat] JOIN [USCongress].[dbo].[CorrectedVotes] as [cv] ON [seat].[NPID] = [cv].[NPID] WHERE [seat].[NPID] = [cv].[NPID] AND [cv].[Party] = ? AND [seat].[OfficeName] = ? AND [cv].[State] = ?

-- second most votes per seat state

SELECT SUM([cv].[Votes]) AS [Votes], [cv].[Party] FROM [USCongress].[dbo].[US-congress-race-2012seats] as [seat], [USCongress].[dbo].[CorrectedVotes] as [cv] WHERE [seat].[NPID] = [cv].[NPID] AND [seat].[OfficeName] = ? AND [seat].[NPID] = [cv].[NPID] AND [cv].[State] = ? AND [cv].[Party] != 'Dem' GROUP BY [cv].[Party]

-- distinct seat per state

SELECT DISTINCT [OfficeName] FROM [USCongress].[dbo].[US-congress-race-2012seats] ORDER BY [OfficeName] ASC

-- distinct state

SELECT DISTINCT [State] FROM [USCongress].[dbo].[US-congress-race-2012seats] ORDER BY [State] ASC

-- seats per state

SELECT COUNT(*) FROM [USCongress].[dbo].[US-congress-race-2012seats] WHERE [OfficeName] = ? AND [State] = ?

-- number of distinct parties with wins

SELECT COUNT(*) FROM (SELECT DISTINCT [Party] FROM [USCongress].[dbo].[CorrectedVotes] WHERE [Winner] = 1) as [C]

-- distinct parties with wins

SELECT DISTINCT [Party] FROM [USCongress].[dbo].[US-congress-race-2012seats] AS [seat] JOIN [USCongress].[dbo].[CorrectedVotes] AS [cv] ON [seat].[NPID] = [cv].[NPID] WHERE ([Party] = 'Dem' OR [Party] = 'GOP') AND [OfficeName] = ? AND [cv].[State] = ?

-- number of party winners

SELECT COUNT(*) FROM [USCongress].[dbo].[CorrectedVotes] [cv], [USCongress].[dbo].[US-congress-race-2012seats] AS [seat] WHERE [cv].[NPID] = [seat].[NPID] AND [cv].[Winner] = 1 AND [Party] = 'Dem' AND [seat].[OfficeName] = ? AND [State] = ?

-- number of total party winners per office

SELECT COUNT(*) FROM [USCongress].[dbo].[CorrectedVotes] [cv], [USCongress].[dbo].[US-congress-race-2012seats] AS [seat] WHERE [cv].[NPID] = [seat].[NPID] AND [cv].[Winner] = 1 AND [Party] = 'Dem' AND [seat].[OfficeName] = ? AND [cv].[State] = ?

-- number of seats per office

SELECT COUNT(*), [OfficeName] FROM [USCongress].[dbo].[US-congress-race-2012seats] GROUP BY [OfficeName] ORDER BY [OfficeName] ASC

-- dem wins per office

SELECT COUNT(*) FROM [USCongress].[dbo].[CorrectedVotes] AS [cv], [USCongress].[dbo].[US-congress-race-2012seats] AS [seat] WHERE [cv].[NPID] = [seat].[NPID] AND [cv].[Party] = 'Dem' AND [cv].[Winner] = 1 AND [seat].[OfficeName] = ?

-- total seats per office

SELECT COUNT(*), [OfficeName] FROM [USCongress].[dbo].[US-congress-race-2012seats] GROUP BY [OfficeName] ORDER BY [OfficeName] ASC

-- state list (alpha order)

-- AK,AL,AR,AZ,CA,CO,CT,DE,FL,GA,HI,IA,ID,IL,IN,KS,KY,LA,MA,MD,ME,MI,MN,MO,MS,MT,NC,ND,NE,NH,NJ,NM,NV,NY,OH,OK,OR,PA,RI,SC,SD,TN,TX,UT,VA,VT,WA,WI,WV,WY
