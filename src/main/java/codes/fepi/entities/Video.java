package codes.fepi.entities;


public class Video {
	private String title;
	private String url;
	private boolean download;

	public Video(String title, String url, boolean download) {
		this.title = title;
		this.url = url;
		this.download = download;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isDownload() {
		return download;
	}

	public void setDownload(boolean download) {
		this.download = download;
	}


	@Override
	public String toString() {
		return "Video{" +
				"title='" + title + '\'' +
				", url='" + url + '\'' +
				", download=" + download +
				'}';
	}

	@Override
	public boolean equals(Object obj) {
		return this.url.equals(((Video) obj).getUrl());
	}
}
