# How to Fetch Server Performance Data from Solarwinds

SolarWinds IT monitoring and management tools are built for SysAdmins and network engineers who need powerful and affordable tools.
SolarWinds saves its data in its RDBMS database most often an MSSQL ones.

## Information about tables holding the data
http://solarwinds.github.io/OrionSDK/schema/ - details all table and column details

We then conclude as below regarding the tables

1. Orion.Nodes -> Contain details of CI (i.e. all hosts) and the their high level attributes. NodeID is unique and serves as primary key.
2. Orion.CPULoad -> Stores the CPU utilization, Memory utilizaton as captured by Solarwinds. This table contains NodeID key which can be joined to Orion.Nodes table to get the hostname
3. Orion.Volumes -> Contain details of all disk and file systems and the their high level attributes. VolumeID is unique and serves as primary key. This table contains NodeID key which can be joined to Orion.Nodes table to get the hostname.
4. Orion.VolumeUsageHistory -> Stores the Disk utilization, File utilizaton as captured by Solarwinds. This table contains VolumeID key which can be joined to Orion.Volumes table to get the Volume name

### Query to retrieve all hosts, OS with version from SolarWindows

SELECT Nodes.Caption,Nodes.Vendor,Nodes.MachineType,Nodes.IOSVersion,Nodes.Status FROM Orion.Nodes

### Query to retrieve the time period for which the data is available in SolarWinds for all hosts - CPU and Memory

SELECT Nodes.Caption,min(CPULoad.datetime) AS mindatetime,max(CPULoad.datetime) as maxdatetime FROM Orion.Nodes INNER JOIN Orion.CPULoad ON (Nodes.NodeID = CPULoad.NodeID) group by Nodes.Caption

### Query to retrieve disk / filesystems for a given host

SELECT Nodes.Caption,Volumes.DisplayName from Orion.Volumes INNER JOIN  Orion.Nodes ON (Nodes.NodeID = Volumes.NodeID) where Volumes.Type not in ('Floppy Disk','RAM','RAM Disk','Virtual Memory','Other') and Nodes.Caption = 'somehost'

### Query to retrieve all disks of all hosts

SELECT Nodes.Caption,Volumes.DisplayName from Orion.Volumes INNER JOIN  Orion.Nodes ON (Nodes.NodeID = Volumes.NodeID) and Volumes.Type not in ('Floppy Disk','RAM','RAM Disk','Virtual Memory','Other') and Nodes.Vendor = 'Windows'

### Query to retrieve all filesystems of all hosts

SELECT Nodes.Caption,Volumes.DisplayName from Orion.Volumes INNER JOIN  Orion.Nodes ON (Nodes.NodeID = Volumes.NodeID) and Volumes.Type not in ('Floppy Disk','RAM','RAM Disk','Virtual Memory','Other') and Nodes.Vendor = 'Linux'


### Query to retrieve memory and CPU utilization i.e. time and value as output for a given host and after a specified time


SELECT Nodes.Caption,CPULoad.datetime,CPULoad.AvgLoad,CPULoad.AvgPercentMemoryUsed FROM Orion.Nodes INNER JOIN Orion.CPULoad ON (Nodes.NodeID = CPULoad.NodeID) where Nodes.Caption = 'somehost' and CPULoad.datetime > '2020-01-12T15:00:00.0000000' order by CPULoad.datetime desc


### Query to retrieve the minimum and maximum time period for which the data is available in SolarWinds for all hosts - Disk and FileSystem


SELECT Nodes.Caption,Volumes.DisplayName,MIN(VolumeUsageHistory.DateTime) AS mindatetime,MAX(VolumeUsageHistory.DateTime) AS maxdatetime from Orion.Nodes INNER JOIN Orion.Volumes  ON (Nodes.NodeID = Volumes.NodeID) INNER JOIN  Orion.VolumeUsageHistory ON (VolumeUsageHistory.VolumeID = Volumes.VolumeID AND VolumeUsageHistory.NodeID = Volumes.NodeID) where Volumes.Type not in ('Floppy Disk','RAM','RAM Disk','Virtual Memory','Other') group by Nodes.Caption,Volumes.DisplayName

### Query to retrieve the minimum and maximum time period for which the data is available in SolarWinds for given host - Disk and FileSystem

SELECT MIN(VolumeUsageHistory.DateTime) AS mindate,MAX(VolumeUsageHistory.DateTime) AS maxdate from Orion.Nodes INNER JOIN Orion.Volumes  ON (Nodes.NodeID = Volumes.NodeID) INNER JOIN  Orion.VolumeUsageHistory ON (VolumeUsageHistory.VolumeID = Volumes.VolumeID AND VolumeUsageHistory.NodeID = Volumes.NodeID) where Volumes.Type not in ('Floppy Disk','RAM','RAM Disk','Virtual Memory','Other') AND Nodes.Caption = 'somehost' AND Volumes.DisplayName LIKE 'C:%'

### Query to retrieve Disk utilization i.e. hostname,volumename,time and value as output for a given host, disk and after a specified time period

SELECT Nodes.Caption,Volumes.DisplayName,VolumeUsageHistory.DateTime,VolumeUsageHistory.DiskSize,VolumeUsageHistory.AvgDiskUsed,VolumeUsageHistory.PercentDiskUsed from Orion.Nodes INNER JOIN Orion.Volumes  ON (Nodes.NodeID = Volumes.NodeID) INNER JOIN  Orion.VolumeUsageHistory ON (VolumeUsageHistory.VolumeID = Volumes.VolumeID AND VolumeUsageHistory.NodeID = Volumes.NodeID) where Volumes.Type not in ('Floppy Disk','RAM','RAM Disk','Virtual Memory','Other') AND Nodes.Caption = 'somehost' AND Volumes.DisplayName LIKE 'C:%' and VolumeUsageHistory.DateTime > '2020-01-12T15:00:00.0000000' order by VolumeUsageHistory.DateTime desc


SELECT Nodes.Caption,Volumes.DisplayName,VolumeUsageHistory.DateTime,VolumeUsageHistory.DiskSize,VolumeUsageHistory.AvgDiskUsed,VolumeUsageHistory.PercentDiskUsed from Orion.Nodes INNER JOIN Orion.Volumes  ON (Nodes.NodeID = Volumes.NodeID) INNER JOIN  Orion.VolumeUsageHistory ON (VolumeUsageHistory.VolumeID = Volumes.VolumeID AND VolumeUsageHistory.NodeID = Volumes.NodeID) where Volumes.Type not in ('Floppy Disk','RAM','RAM Disk','Virtual Memory','Other') AND Nodes.Caption = 'somehost' AND Volumes.DisplayName LIKE '/boot' and 
Code for connect to SolarWinds REST APIs
 
