<?php
 

 
class DB_Functions {
 
    private $conn;
 
    
    function __construct() {
        require_once 'DB_Connect.php';
        
        $db = new Db_Connect();
        $this->conn = $db->connect();
    }
 
    
    function __destruct() {
         
    }
 
 
 public function storiesindexbygenre($genre,$number){
	 $return_arr = array();
	 $result=array();
	 $stmt = $this->conn->prepare("SELECT * FROM main WHERE genre = ? LIMIT ?");
        $stmt->bind_param("ss",$genre,$number);
		$stmt->execute();
		$result = $stmt->get_result();
		foreach($result as $row) {
		
		$row_array['_id'] = $row['_id'];
		$row_array['chat_uid'] = $row['chat_uid'];
		$row_array['title'] = $row['title'];
		$row_array['author_name'] = $row['author_name'];
		$row_array['cover_image'] = $row['cover_image'];
		$row_array['views'] = $row['views'];
		$row_array['genre'] = $row['genre'];
		$row_array['datetime'] = $row['datetime'];
		$row_array['episode'] = $row['episode'];
		$row_array['updated'] = $row['updated'];
		$row_array['description'] = $row['description'];
		
		
		
		array_push($return_arr,$row_array);
		
		
		
		}
        $stmt->close();
		echo json_encode($return_arr);
		
 }
 public function allstories($last){
	 $return_arr = array();
	 $result=array();
	 $stmt = $this->conn->prepare("SELECT * FROM main WHERE chat_uid > ?");
	  $stmt->bind_param("s",$last);
		$stmt->execute();
		$result = $stmt->get_result();
		foreach($result as $row) {
		
		$row_array['_id'] = $row['_id'];
		$row_array['chat_uid'] = $row['chat_uid'];
		$row_array['title'] = $row['title'];
		$row_array['author_name'] = $row['author_name'];
		$row_array['cover_image'] = $row['cover_image'];
		$row_array['views'] = $row['views'];
		$row_array['genre'] = $row['genre'];
		$row_array['datetime'] = $row['datetime'];
		$row_array['episode'] = $row['episode'];
		$row_array['updated'] = $row['updated'];
		$row_array['description'] = $row['description'];
		
		
		
		array_push($return_arr,$row_array);
		
		
		
		}
        $stmt->close();
		echo json_encode($return_arr);
		
 }
 public function storiesindexbyviews($number){
	 $return_arr = array();
	 $result=array();
	 $stmt = $this->conn->prepare("SELECT * FROM main ORDER BY views DESC LIMIT ?");
        $stmt->bind_param("s",$number);
		$stmt->execute();
		$result = $stmt->get_result();
		foreach($result as $row) {
		
		$row_array['_id'] = $row['_id'];
		$row_array['chat_uid'] = $row['chat_uid'];
		$row_array['title'] = $row['title'];
		$row_array['author_name'] = $row['author_name'];
		$row_array['cover_image'] = $row['cover_image'];
		$row_array['views'] = $row['views'];
		$row_array['genre'] = $row['genre'];
		$row_array['datetime'] = $row['datetime'];
		$row_array['episode'] = $row['episode'];
		$row_array['updated'] = $row['updated'];
		$row_array['description'] = $row['description'];
		
		
		
		array_push($return_arr,$row_array);
		
		
		
		}
        $stmt->close();
		echo json_encode($return_arr);
		
 }
 
  public function getchatstorybyuid($c_uid){
	 
	 $return_arr = array();
	 $result=array();
	 $stmt = $this->conn->prepare("SELECT * FROM chats WHERE chat_uid = ? ORDER BY sequence");
        $stmt->bind_param("s",$c_uid);
		$stmt->execute();
	  
		$result = $stmt->get_result();
		foreach($result as $row) {
		$row_array['_id'] = $row['_id'];
		$row_array['chat_uid'] = $row['chat_uid'];
		$row_array['sequence'] = $row['sequence'];
		$row_array['typing'] = $row['typing'];
		$row_array['sender_name'] = $row['sender_name'];
		$row_array['message'] = $row['message'];
		$row_array['sender_color'] = $row['sender_color'];
		
		
		array_push($return_arr,$row_array);
		
		
		
		}
	  
        $stmt->close();
		echo json_encode($return_arr);
		
 }
 public function getviews(){
	 $return_arr = array();
	 $result=array();
	 $stmt = $this->conn->prepare("SELECT * FROM main");
        
		$stmt->execute();
		$result = $stmt->get_result();
		foreach($result as $row) {
		
		
		$row_array['chat_uid'] = $row['chat_uid'];
		$row_array['views'] = $row['views'];
		
		
		
		array_push($return_arr,$row_array);
		
		
		
		}
        $stmt->close();
		echo json_encode($return_arr);
		
 }
  public function getversion(){
	 $return_arr = array();
	 $result=array();
	 $stmt = $this->conn->prepare("SELECT version FROM app_version ORDER BY sequence LIMIT 1");
        
		$stmt->execute();
		$result = $stmt->get_result();
		foreach($result as $row) {
		
		
		echo $row_array['version'];
	
		
		
		
		}
        
		
 }

  public function updateview($c_uid,$views){
	 $result=array();
	 $stmt = $this->conn->prepare("UPDATE main SET views = views+? WHERE chat_uid = ?");
        $stmt->bind_param("ss",$views,$c_uid);
		$stmt->execute();
		$result = $stmt->get_result();
        $stmt->close();
		$stmt = $this->conn->prepare("SELECT * FROM main where chat_uid = ?");
		$stmt->bind_param("s",$c_uid);
		$stmt->execute();
		$result = $stmt->get_result();
		foreach($result as $row) {
		
		
		$row_array['chat_uid'] = $row['chat_uid'];
		$row_array['views'] = $row['views'];
		if($row['chat_uid']==$c_uid){
			echo $views,"v",$row['views'];
		}else{
			echo "0,0";
		}
		
		}
		
		
 }
    
    public function storeUser($name,$roll,$pass,$phone,$email) {
        $stmt = $this->conn->prepare("INSERT INTO students(name,roll,password,phone,email, created_at) VALUES(?, ?, ?, ?, ?, NOW())");
        $stmt->bind_param("sssss", $name, $roll, $pass, $phone, $email);
        $result = $stmt->execute();
        $stmt->close();
		
       
        if ($result) {
            $stmt = $this->conn->prepare("SELECT * FROM students WHERE roll = ?");
            $stmt->bind_param("s", $roll);
            $stmt->execute();
            $user = $stmt->get_result()->fetch_assoc();
            $stmt->close();
 
            return $user;
        } else {
            return false;
        }
    }
	
	 public function UpdateUser($name,$roll,$phone,$email) {
		 $none="none";
        $stmt = $this->conn->prepare("UPDATE students SET name=?,phone=?,email=?,verified_by=? WHERE roll=?");
        $stmt->bind_param("sssss", $name, $phone, $email,$none, $roll);
        $result = $stmt->execute();
        $stmt->close();
		
       
        if ($result) {
			
            $stmt = $this->conn->prepare("SELECT * FROM students WHERE roll = ?");
            $stmt->bind_param("s", $roll);
            $stmt->execute();
            $user = $stmt->get_result()->fetch_assoc();
            $stmt->close();
 
            return $user;
        } else {
            return false;
        }
    }
 
    public function getUserByRollAndPassword($roll, $password) {
 
        $stmt = $this->conn->prepare("SELECT * FROM students WHERE roll = ?");
 
        $stmt->bind_param("s", $roll);
 
        if ($stmt->execute()) {
            $user = $stmt->get_result()->fetch_assoc();
            $stmt->close();
 
            
          
            if ($user['password'] == $password) {
               
                return $user;
            }
        } else {
            return NULL;
        }
    }
	public function VerifyUser($roll_verify,$roll) {
		$stmt = $this->conn->prepare("SELECT * FROM students WHERE roll = ?");
		$stmt->bind_param("s", $roll);
		if ($stmt->execute()) {
            $user = $stmt->get_result()->fetch_assoc();
			$stmt->close();
			
            if ($user['verified_by'] != "none") {
 			$stmt = $this->conn->prepare("UPDATE students SET verified_by= ? WHERE roll= ?");
			$stmt->bind_param("ss", $roll,$roll_verify);
			if ($stmt->execute()) {
            return "done";            
        }else{
			return "error";
		}
			}else{
			return "not verified";
		}
		}else {
            return NULL;
        }
    }
	
	
	
	
	
public function getUsersByQuery($query1,$query2,$last) {
 
        $stmt = $this->conn->prepare("SELECT * FROM students WHERE (roll like '%$query1%' or roll like '%$query2%'or name like '%$query1%' or name like '%$query2%') and roll > '$last' order by 'name';");
 
        
 
        if ($stmt->execute()) {
            $user = $stmt->get_result()->fetch_assoc();
            $stmt->close();
                return $user;
            }
         else {
            return NULL;
        }
    }
 
    
    public function isUserExisted($roll) {
        $stmt = $this->conn->prepare("SELECT roll from students WHERE roll = ?");
 
        $stmt->bind_param("s", $roll);
 
        $stmt->execute();
 
        $stmt->store_result();
 
        if ($stmt->num_rows > 0) {
            
            $stmt->close();
            return true;
        } else {
            
            $stmt->close();
            return false;
        }
    }
 
    
    
 
}
 
?>