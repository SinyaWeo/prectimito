<html lang="cs">
	<head>
		<title>Přečti mi to - Twitter Bot</title>
		<link rel="icon" type="image/png" href="favicon.ico">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	</head>
	<body>

		<?php
            $database = "/../../data/twitterbot/resources/database/twitterbot.db";

            $_GET = filter_input_array(INPUT_GET, FILTER_SANITIZE_STRING);

            $id = $_GET["id"];

            $tweetText = getTweetText($id, $database);

            if (empty($tweetText)) {
                echo "Tento tweet v databázi nemám :(";
            } else {
                echo "Text tweetu:<p>" . nl2br(htmlspecialchars($tweetText['tweet_text']), ENT_QUOTES) . "</p>";
            }

            function getTweetText(String $id, String $database) {
                
                $db = new PDO('sqlite:' . __DIR__ . $database);

                $stmt = $db->prepare("SELECT tweet_text FROM tweets WHERE tweet_id=? LIMIT 1");
                $stmt->execute([$id]);

                return $stmt->fetch();
            }
        ?>
	</body>
</html>
