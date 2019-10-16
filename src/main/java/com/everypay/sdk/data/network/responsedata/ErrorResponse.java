package com.everypay.sdk.data.network.responsedata;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ErrorResponse{
	@Expose
	@SerializedName("error")
	private Error mError;

	public Error getError() {
		return mError;
	}

	public void setError(Error mError) {
		this.mError = mError;
	}

	public static class Error {
		@Expose
		@SerializedName("message")
		private String mMessage;
		@Expose
		@SerializedName("code")
		private int mCode;

		public String getMessage() {
			return mMessage;
		}

		public void setMessage(String mMessage) {
			this.mMessage = mMessage;
		}

		public int getCode() {
			return mCode;
		}

		public void setCode(int mCode) {
			this.mCode = mCode;
		}
	}
}