package be.business.connector.recipe.executor.domain;

import java.util.Calendar;

/**
 * The Class ListNotificationsItem.
 */
public class ListNotificationsItem extends 
be.recipe.client.services.executor.ListNotificationsItem{	
	
	/**
	 * Gets the linked exception.
	 *
	 * @return the linked exception
	 */
	public Throwable getLinkedException() {
		return linkedException;
	}

	public ListNotificationsItem(be.recipe.client.services.executor.ListNotificationsItem root) {
		super();
		this.root = root;
	}

	public ListNotificationsItem() {
		super();
	}

	/**
	 * Sets the linked exception.
	 *
	 * @param linkedException the new linked exception
	 */
	public void setLinkedException(Throwable linkedException) {
		this.linkedException = linkedException;
	}

	/** The root. */
	be.recipe.client.services.executor.ListNotificationsItem root = null;

	/** The linked exception. */
	Throwable linkedException = null;

	/**
	 * Gets the content.
	 *
	 * @return the content
	 */
	@Override
	public byte[] getContent() {
		if( linkedException != null ){
			throw new RuntimeException(linkedException);
		}
		return root.getContent();
	}

	/**
	 * Gets the sent by.
	 *
	 * @return the sent by
	 */
	@Override
	public Long getSentBy() {
		return root.getSentBy();
	}

	/**
	 * Gets the sent date.
	 *
	 * @return the sent date
	 */
	@Override
	public Calendar getSentDate() {
		return root.getSentDate();
	}

	/**
	 * Sets the content.
	 *
	 * @param arg0 the new content
	 */
	@Override
	public void setContent(byte[] arg0) {
		root.setContent(arg0);
	}

	/**
	 * Sets the sent by.
	 *
	 * @param arg0 the new sent by
	 */
	@Override
	public void setSentBy(Long arg0) {
		root.setSentBy(arg0);
	}

	/**
	 * Sets the sent date.
	 *
	 * @param arg0 the new sent date
	 */
	@Override
	public void setSentDate(Calendar arg0) {
		root.setSentDate(arg0);
	}

	@Override
	public boolean equals(Object obj) {
		return root.equals(obj);
	}

	@Override
	public int hashCode() {
		return root.hashCode();
	}
	
	@Override
	public String toString() {
		return root.toString();
	}
	 
	
}
