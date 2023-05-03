package cz.nos.twitterbot;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.TimeZone;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;

import javax.imageio.ImageIO;

import org.apache.commons.lang.StringEscapeUtils;

import io.github.redouane59.twitter.TwitterClient;
import io.github.redouane59.twitter.dto.endpoints.AdditionalParameters;
import io.github.redouane59.twitter.dto.tweet.Tweet;
import io.github.redouane59.twitter.dto.tweet.TweetList;
import io.github.redouane59.twitter.dto.tweet.TweetParameters;
import io.github.redouane59.twitter.dto.tweet.TweetParameters.Reply;
import io.github.redouane59.twitter.dto.tweet.entities.MediaEntity;
import io.github.redouane59.twitter.signature.TwitterCredentials;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class TwitterBot {

	private static final Logger logger = Logger.getLogger(TwitterBot.class.getName());

	public static void main(String[] args) throws BackingStoreException, IOException, SQLException {

		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

		Config config = new Config();

		Database database = new Database();

		TwitterClient twitterClient = new TwitterClient(TwitterCredentials.builder()
				.accessToken(config.getAccessToken()).accessTokenSecret(config.getAccessTokenSecret())
				.apiKey(config.getConsumerKey()).apiSecretKey(config.getConsumerKeySecret()).build());

		String userName = "@" + twitterClient.getUserFromUserId(config.getUserId()).getName();

		AdditionalParameters additionalParameters = AdditionalParameters.builder()
				.startTime(config.getLastCheckedDate()).build();
		TweetList tweetList = twitterClient.getUserMentions(config.getUserId(), additionalParameters);
		
		for (var tweet : tweetList.getData()) {
			// Find a parent tweet ID
			Tweet parentTweet = twitterClient.getTweet(tweet.getInReplyToStatusId());
			logger.info("Parent tweet: " + parentTweet.getText());
			if (!parentTweet.getText().toLowerCase().contains(userName.toLowerCase())
					&& parentTweet.getMedia() != null) {
				for (MediaEntity media : parentTweet.getMedia()) {

					String tweetText = "";
					URL url = new URL(media.getUrl());

					String contentType = getContentType(media.getUrl());

					logger.info("Content type: " + contentType);
					BufferedImage image = ImageIO.read(url);

					String tesseractResult = getTesseractData(image);

					if (tesseractResult.isBlank()) {
						tweetText = "Obrázek bohužel neobsahuje žádný text...";
					} else {
						String sanitizedText = StringEscapeUtils.escapeSql(tesseractResult);
						Boolean storeResult = database.storeTweet(parentTweet.getId(), sanitizedText);
						if (!storeResult) {
							logger.severe("Tweet store error: " + parentTweet.getId());
						}
						tweetText = "Tady je odkaz na text z obrázku:\n\n" + "https://readerbot.nosoftware.cz/tweet/"
								+ parentTweet.getId();
					}

					TweetParameters parameters = TweetParameters.builder().text(tweetText)
							.reply(Reply.builder().inReplyToTweetId(tweet.getId()).build()).build();
					Tweet resultPost = twitterClient.postTweet(parameters);
					if (resultPost.getId() != null) {
						logger.severe("Tweet post error: " + parentTweet.getId());
					}

				}
			}
		}

		config.setLastCheckedDate(LocalDateTime.now());

	}

	private static String getTesseractData(BufferedImage image) {
		Tesseract tesseract = new Tesseract();
		String tesseractResult = "";

		tesseract.setDatapath("resources/tessdata");
		tesseract.setLanguage("ces");
		tesseract.setPageSegMode(6);
		tesseract.setOcrEngineMode(1);

		try {
			tesseractResult = tesseract.doOCR(image);
		} catch (TesseractException e) {
			logger.severe("Tesseract error: " + e.toString());
		}

		return tesseractResult;
	}

	public static String getContentType(String urlString) throws IOException {
		URL url = new URL(urlString);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("HEAD");
		if (isRedirect(connection.getResponseCode())) {
			String newUrl = connection.getHeaderField("Location"); // get redirect url from "location" header field
			logger.info("Original request URL: " + urlString + " redirected to: " + newUrl);
			return getContentType(newUrl);
		}
		return connection.getContentType();
	}

	protected static boolean isRedirect(int statusCode) {

			return statusCode != HttpURLConnection.HTTP_OK 
				&& (statusCode == HttpURLConnection.HTTP_MOVED_TEMP 
					|| statusCode == HttpURLConnection.HTTP_MOVED_PERM
					|| statusCode == HttpURLConnection.HTTP_SEE_OTHER);
	}
}
