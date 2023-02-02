package com.raman.FileProtector;

public interface ProgressMeasure
{
	void onProgressUpdate(int progress,String type);
    void onComplete(int type);
}
