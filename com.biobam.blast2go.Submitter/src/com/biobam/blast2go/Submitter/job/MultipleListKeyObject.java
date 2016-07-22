package com.biobam.blast2go.Submitter.job;
import java.util.ArrayList;
import java.util.List;

import com.biobam.blast2go.api.job.parameters.keys.internal.ParameterKey;

/**
 * @author alex
 */
public final class MultipleListKeyObject extends ParameterKey<ListKeyObject[]> {
	/**
	 * Default key value. Also one of the key elements.
	 */
	private final ListKeyObject[] defaultValue;

	/**
	 * Parser to convert from string to Object.
	 */
	private final IListKeyObjectParser<ListKeyObject> parser;

	/**
	 * Private Builder for the ListKey.
	 *
	 * @param builder
	 * @See {@link Builder}
	 */
	private MultipleListKeyObject(final Builder builder) {
		super(builder);
		defaultValue = builder.defaultValue;
		parser = builder.parser;
	}

	/**
	 * Getter for the key default value.
	 *
	 * @return the default value.
	 */
	public final ListKeyObject[] getDefaultValue() {
		return defaultValue;
	}

	/**
	 * {@link MultipleListKeyObject} builder.
	 *
	 */
	public static class Builder extends ParameterKey.Builder<Builder, ListKeyObject[]> {

		/**
		 * Parser to convert from string to Object.
		 */
		private final IListKeyObjectParser<ListKeyObject> parser;

		/**
		 * The default value to be set in the new key.
		 */
		private final ListKeyObject[] defaultValue;

		/**
		 * Protected {@link Builder} constructor including the list values and the default selected option.
		 *
		 * @param id
		 *            Unique identifier.
		 * @param defaultKeyValue
		 *            Default key values.
		 */
		private Builder(final String id, final IListKeyObjectParser<ListKeyObject> parser, final ListKeyObject[] defaultKeyValue) {
			super(id);
			if (parser == null) {
				throw new NullPointerException("Parser can not be null");
			}
			if (defaultKeyValue == null) {
				throw new NullPointerException("Default value can not be null");
			}
			defaultValue = defaultKeyValue;
			this.parser = parser;
		}

		@Override
		public MultipleListKeyObject build() {
			final MultipleListKeyObject key = new MultipleListKeyObject(this);
			return key;
		}
	}

	/**
	 * Create a ListKey using a builder. The values parameter is a {@link List}.
	 *
	 * @param id
	 *            Unique key identifier.
	 * @param defaultValue
	 *            The default values;
	 *
	 * @return A {@link MultipleListKeyObject} Builder.
	 */
	public static Builder builder(final String id, final IListKeyObjectParser<ListKeyObject> parser, final ListKeyObject[] defaultValue) {
		final Builder builder = new Builder(id, parser, defaultValue);
		return builder;
	}

	public static Builder builder(final String id, final IListKeyObjectParser<ListKeyObject> parser) {
		return builder(id, parser, new ListKeyObject[0]);
	}

	@Override
	public boolean setValueFromString(final String stringValue) {
		if (stringValue == null) {
			throw new NullPointerException();
		}
		final String[] objectStringValues = stringValue.split(",");
		final List<ListKeyObject> objectList = new ArrayList<ListKeyObject>();
		for (final String objectStringValue : objectStringValues) {
			final ListKeyObject object = parser.parseFromString(objectStringValue);
			if (object != null) {
				objectList.add(object);
			}
		}
		setValue(objectList.toArray(new ListKeyObject[objectList.size()]));
		if (objectList.isEmpty()) {
			return false;
		}
		return true;
	}

	@Override
	public String defaultValueString() {
		final StringBuilder sb = new StringBuilder();
		for (final ListKeyObject object : getDefaultValue()) {
			if (sb.length() > 0) {
				sb.append(",");
			}
			sb.append(object.toStringFormat());
		}
		return sb.toString();
	}

	@Override
	public String getStringFromValue() {
		final StringBuilder sb = new StringBuilder();
		for (final ListKeyObject object : getValue()) {
			if (sb.length() > 0) {
				sb.append(",");
			}
			sb.append(object.toStringFormat());
		}
		return sb.toString();
	}

}
