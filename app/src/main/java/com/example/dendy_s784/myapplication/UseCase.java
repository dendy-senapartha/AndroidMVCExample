package com.example.dendy_s784.myapplication;

public abstract class UseCase<request extends UseCase.RequestValues, response extends UseCase.ResponseValue>
{
    private request mRequestValues;

    private UseCaseCallback<response> mUseCaseCallback;

    public void setRequestValues(request requestValues) {
        mRequestValues = requestValues;
    }

    public request getRequestValues() {
        return mRequestValues;
    }

    public UseCaseCallback<response> getUseCaseCallback() {
        return mUseCaseCallback;
    }

    public void setUseCaseCallback(UseCaseCallback<response> useCaseCallback) {
        mUseCaseCallback = useCaseCallback;
    }

    void run() {
        executeUseCase(mRequestValues);
    }

    protected abstract void executeUseCase(request requestValues);

    /**
     * Data passed to a request.
     */
    public interface RequestValues {
    }

    /**
     * Data received from a request.
     */
    public interface ResponseValue {
    }

    public interface UseCaseCallback<R> {
        void onSuccess(R response);
        void onError();
    }
}
