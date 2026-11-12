CREATE TABLE Users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN','USER','MODERATOR') NOT NULL DEFAULT 'USER'
);

CREATE TABLE Branch (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    BranchName VARCHAR(255) NOT NULL
);

CREATE TABLE AuctionReceived (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    ItemName VARCHAR(255) NOT NULL,
    Quantity INT NOT NULL,
    LetterNo VARCHAR(100),
    ReceiveDate DATE,
    Remarks VARCHAR(500)
);

CREATE TABLE AuctionInventory (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    ItemName VARCHAR(255) UNIQUE,
    Quantity INT NOT NULL
);

CREATE TABLE AuctionRemaining (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    ItemName VARCHAR(255) UNIQUE,
    Quantity INT NOT NULL
);

CREATE TABLE ReceivedFrom (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    TypeName VARCHAR(255) NOT NULL
);

CREATE TABLE StoreReceived (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    ItemName VARCHAR(255) NOT NULL,
    Quantity INT NOT NULL,
    ReceivedFromID INT NOT NULL,
    ReceiveDate DATE,
    UnitPrice INT,
    Remarks VARCHAR(500),
    FOREIGN KEY (ReceivedFromID) REFERENCES ReceivedFrom(ID)
);

CREATE TABLE MinuteSheet (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    PurchaseOrderNo VARCHAR(255) NOT NULL,
    Vendor VARCHAR(255) NOT NULL,
    StoreReceivedID INT,
    FOREIGN KEY (StoreReceivedID) REFERENCES StoreReceived(ID)
);

CREATE TABLE PettyCash (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    VoucherNo VARCHAR(255) NOT NULL,
    StoreReceivedID INT,
    FOREIGN KEY (StoreReceivedID) REFERENCES StoreReceived(ID)
);

CREATE TABLE Replacement (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    BranchID INT NOT NULL,
    StoreReceivedID INT,
    FOREIGN KEY (StoreReceivedID) REFERENCES StoreReceived(ID),
	FOREIGN KEY (BranchID) REFERENCES Branch(ID)
);

CREATE TABLE StoreInventory (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    ItemName VARCHAR(255) UNIQUE,
    Quantity INT NOT NULL
);

CREATE TABLE StoreRemaining (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    ItemName VARCHAR(255) UNIQUE,
    Quantity INT NOT NULL
);

CREATE TABLE DHAEmployee (
	ID INT AUTO_INCREMENT PRIMARY KEY,
    DHAID  VARCHAR(20) UNIQUE,
    EmpName VARCHAR(255),
    BranchID INT,
    Designation VARCHAR(255),
    Scale INT,
    FOREIGN KEY (BranchID) REFERENCES Branch(ID)
);

CREATE TABLE UserStatus (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    StatusName VARCHAR(255) NOT NULL
);

CREATE TABLE UserReceived (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    ItemName VARCHAR(255) NOT NULL,
    Quantity INT NOT NULL,
    PurchaseOrderNo VARCHAR(255) NOT NULL,
    Vendor VARCHAR(255) NOT NULL,
    ReceiveDate DATE,
    DHAID VARCHAR(20),
    BranchID INT,
    WarrantyDemand VARCHAR(255),
    WarrantyProvided VARCHAR(255),
    StatusID INT,
    Reason VARCHAR(255) NOT NULL,
    Remarks VARCHAR(500),
	FOREIGN KEY (BranchID) REFERENCES Branch(ID),
    FOREIGN KEY (DHAID) REFERENCES DHAEmployee(DHAID),
    FOREIGN KEY (StatusID) REFERENCES UserStatus(ID)
);

CREATE TABLE Issued (
    UserID INT PRIMARY KEY,
    IssuedDate DATE,
    FOREIGN KEY (UserID) REFERENCES UserReceived(ID)
);

CREATE TABLE Pending (
    UserID INT PRIMARY KEY,
    Quantity INT ,
    FOREIGN KEY (UserID) REFERENCES UserReceived(ID)
);

CREATE TABLE AuctionIssued (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    AuctionID INT,
    Quantity INT NOT NULL,
    IssuedDate DATE,
    Remarks VARCHAR(255),
    FOREIGN KEY (AuctionID) REFERENCES AuctionInventory(ID)
);

CREATE TABLE UserIssued (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    UserID INT NOT NULL,
    IssuedDate DATE,
    Remarks VARCHAR(255),
    FOREIGN KEY (UserID) REFERENCES UserReceived(ID)
);

CREATE TABLE IssueType (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    TypeName VARCHAR(255)
);

CREATE TABLE StoreIssued (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    StoreInventoryID INT NOT NULL,
    Quantity INT NOT NULL,
    DHAID VARCHAR(20) NOT NULL,
    IssuedDate DATE,
    IssueTypeID INT,
    RegisterNo VARCHAR(255),
    Remarks VARCHAR(255),
    FOREIGN KEY (StoreInventoryID) REFERENCES StoreInventory(ID),
    FOREIGN KEY (DHAID) REFERENCES DHAEmployee(DHAID),
    FOREIGN KEY (IssueTypeID) REFERENCES IssueType(ID)
);

CREATE TABLE StoreReturn (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    StoreIssueID INT NOT NULL,
    Quantity INT NOT NULL,
    ReturnDate DATE,
    FOREIGN KEY (StoreIssueID) REFERENCES StoreIssued(ID)
);

CREATE TABLE LowStock (
    ItemName VARCHAR(255) PRIMARY KEY,
    LowStockLimit INT DEFAULT 2,
    FOREIGN KEY (ItemName) REFERENCES StoreInventory(ItemName)
);