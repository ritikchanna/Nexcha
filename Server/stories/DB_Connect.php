<?php
class DB_Connect {
    private $conn;
 
    
    public function connect() {
        require_once "Config.php";
         
       
        $this->conn = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE);
        mysqli_set_charset($this->conn, "utf8"); 
        
        return $this->conn;
    }
}
 
?>