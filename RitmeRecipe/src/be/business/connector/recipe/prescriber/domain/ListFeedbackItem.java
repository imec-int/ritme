package be.business.connector.recipe.prescriber.domain;

import java.util.Calendar;

/**
 * The Class ListFeedbackItem.
 */
public class ListFeedbackItem extends be.recipe.client.services.prescriber.ListFeedbackItem {

	
	
	/** The root. */
	be.recipe.client.services.prescriber.ListFeedbackItem root = null;

	/** The linked exception. */
	Throwable linkedException = null;
	
	
	/**
	 * Gets the linked exception.
	 *
	 * @return the linked exception
	 */
	public Throwable getLinkedException() {
		return linkedException;
	}

	/**
	 * Sets the linked exception.
	 *
	 * @param linkedException the new linked exception
	 */
	public void setLinkedException(Throwable linkedException) {
		this.linkedException = linkedException;
	}

	/**
	 * Instantiates a new list feedback item.
	 *
	 * @param root the root
	 */
	public ListFeedbackItem(be.recipe.client.services.prescriber.ListFeedbackItem root) {
		super();
		this.root = root;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return root.equals(obj);
	}

	/* (non-Javadoc)
	 * @see be.recipe.client.services.prescriber.ListFeedbackItem#getContent()
	 */
	@Override
	public byte[] getContent() {
		if( linkedException != null ){
			throw new RuntimeException(linkedException);
		}
		return root.getContent();
	}

	/* (non-Javadoc)
	 * @see be.recipe.client.services.prescriber.ListFeedbackItem#getRid()
	 */
	@Override
	public String getRid() {
		return root.getRid();
	}

	/* (non-Javadoc)
	 * @see be.recipe.client.services.prescriber.ListFeedbackItem#getSentBy()
	 */
	@Override
	public Long getSentBy() {
		return root.getSentBy();
	}

	/* (non-Javadoc)
	 * @see be.recipe.client.services.prescriber.ListFeedbackItem#getSentDate()
	 */
	@Override
	public Calendar getSentDate() {
		return root.getSentDate();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return root.hashCode();
	}

	/* (non-Javadoc)
	 * @see be.recipe.client.services.prescriber.ListFeedbackItem#setContent(byte[])
	 */
	@Override
	public void setContent(byte[] arg0) {
		root.setContent(arg0);
	}

	/* (non-Javadoc)
	 * @see be.recipe.client.services.prescriber.ListFeedbackItem#setRid(java.lang.String)
	 */
	@Override
	public void setRid(String arg0) {
		root.setRid(arg0);
	}

	/* (non-Javadoc)
	 * @see be.recipe.client.services.prescriber.ListFeedbackItem#setSentBy(java.lang.Long)
	 */
	@Override
	public void setSentBy(Long arg0) {
		root.setSentBy(arg0);
	}

	/* (non-Javadoc)
	 * @see be.recipe.client.services.prescriber.ListFeedbackItem#setSentDate(java.util.Calendar)
	 */
	@Override
	public void setSentDate(Calendar arg0) {
		root.setSentDate(arg0);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return root.toString();
	}
	
	
	
}
