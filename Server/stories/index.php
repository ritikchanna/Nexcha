<?php
require_once "DB_Functions.php";
$db = new DB_Functions();

 
 if (isset($_GET['op'])) {
	 $op = $_GET['op'];
	 if($op=="refresh"){
		if (isset($_GET['last'])) { 
	 $db->allstories($_GET['last']);}
	 }
	 elseif($op=="fetch_chat"){
		 if (isset($_GET['chat_uid'])) { 
	 $db->getchatstorybyuid($_GET['chat_uid']);}
	 }
	 elseif($op=="getallnew"){
		 if (isset($_GET['last_chat'])) {
			 $db->allstories($_GET['last_chat']);
		 }
	 }
	 elseif($op=="update_view"){
		 if (isset($_GET['chat_uid'])&&isset($_GET['inc_view'])) {
			 $db->updateview($_GET['chat_uid'],$_GET['inc_view']);
		 }
	 }
		 elseif($op=="getviews"){		
			 $db->getviews();		 
	 }
	 elseif($op=="getviews"){		
			 $db->getversion();		 
	 }
	 else{
		 $response["error"] = TRUE;
		$response["error_msg"] = "Undefined operation";
	 }
	 
 }
 else {
   
    $response["error"] = TRUE;
    $response["error_msg"] = "Required parameters missing!";
    echo json_encode($response);
}

?>