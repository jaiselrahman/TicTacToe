<?php
$source = urldecode($_POST['source']);

if($source != "app")
		header("Location: /index.php");
$db=new SQLite3("~db/xo.db",SQLITE3_OPEN_READWRITE|SQLITE3_OPEN_CREATE. $error) 
						or die("Failed: $error");
$move=urldecode($_POST['move']);
$action = urldecode($_POST['action']);
$name = urldecode($_POST['name']);
$oppname = urldecode($_POST['oppname']);
$letter = urldecode($_POST['letter']);
if($action == 'get')
{
	$result=$db->query("SELECT move FROM player WHERE name ='" . $oppname . "'");
	$no=$result->fetchArray(SQLITE3_ASSOC);
	echo $no['move'];
}
else
if($action == 'set')
{
	$db->query("UPDATE player SET move=" . $move . " where name = '" . $name . "'");
}
else
if($action== 'gamefinished')
{
    $db->query("UPDATE player SET move=0 where name = '" . $name . "'");
}
else
if($action == 'gamestarted')
{
	$db->query("UPDATE player SET ' WHERE name= '" . $name . "'");
}
unset($db);
?>

