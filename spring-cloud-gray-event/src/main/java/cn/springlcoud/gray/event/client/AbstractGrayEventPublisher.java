package cn.springlcoud.gray.event.client;

import cn.springcloud.gray.retriever.GenericRetriever;
import cn.springlcoud.gray.event.ErrorHandler;
import cn.springlcoud.gray.event.GrayEvent;
import cn.springlcoud.gray.event.LoggingErrorHandler;

import java.util.Collection;
import java.util.List;

/**
 * @author saleson
 * @date 2020-01-30 13:25
 */
public abstract class AbstractGrayEventPublisher implements GrayEventPublisher {

    //    private final Map<ListenerCacheKey, ListenerRetriever> retrieverCache = new ConcurrentHashMap<>(64);
    private List<GrayEventListener> grayEventListeners;
    private ErrorHandler errorHandler = new LoggingErrorHandler();
    private GenericRetriever<GrayEventListener<?>> genericRetriever;


    public AbstractGrayEventPublisher(List<GrayEventListener> grayEventListeners) {
        this.grayEventListeners = grayEventListeners;
        genericRetriever = new GenericRetriever(grayEventListeners, GrayEventListener.class);
    }

    @Override
    public void publishEvent(GrayEvent grayEvent) {
//        Collection<GrayEventListener<?>> grayEventListeners = getGrayEventListeners(grayEvent);
        Collection<GrayEventListener<?>> grayEventListeners = genericRetriever.retrieveFunctions(grayEvent);
        grayEventListeners.forEach(listener -> invokeListenOnEvent(listener, grayEvent));
    }

    protected void invokeListenOnEvent(GrayEventListener listener, GrayEvent grayEvent) {
        try {
            listener.onEvent(grayEvent);
        } catch (Exception e) {
            errorHandler.handleError(e);
        }
    }


//    protected Collection<GrayEventListener<?>> getGrayEventListeners(GrayEvent grayEvent) {
//        Class<?> eventCls = grayEvent.getClass();
//        ListenerCacheKey cacheKey = new ListenerCacheKey(eventCls);
//        ListenerRetriever listenerRetriever = retrieverCache.get(cacheKey);
//        if (Objects.isNull(listenerRetriever)) {
//            synchronized (this) {
//                listenerRetriever = retrieverCache.get(cacheKey);
//                if (!Objects.isNull(listenerRetriever)) {
//                    return listenerRetriever.getListeners();
//                }
//                listenerRetriever = new ListenerRetriever();
//                Collection<GrayEventListener<?>> listeners =
//                        retrieveGrayEventListeners(eventCls, listenerRetriever);
//                retrieverCache.put(cacheKey, listenerRetriever);
//                return listeners;
//            }
//        }
//        return listenerRetriever.getListeners();
//    }
//
//    private Collection<GrayEventListener<?>> retrieveGrayEventListeners(Class<?> eventCls, ListenerRetriever listenerRetriever) {
//        List<GrayEventListener<?>> linstenerList = new ArrayList<>(grayEventListeners.size() / 2);
//        for (GrayEventListener listener : grayEventListeners) {
//            if (supportsEvent(listener, eventCls)) {
//                linstenerList.add(listener);
//            }
//        }
//        AnnotationAwareOrderComparator.sort(linstenerList);
//        listenerRetriever.listeners.addAll(linstenerList);
//        return linstenerList;
//    }
//
//
//    protected boolean supportsEvent(GrayEventListener<?> grayEventListener, Class<?> eventType) {
//        return GenericMatchUtils.match(grayEventListener, GrayEventListener.class, eventType);
//    }
//
//
//    @AllArgsConstructor
//    @Data
//    protected class ListenerCacheKey {
//        private Class<?> eventType;
//    }
//
//    protected class ListenerRetriever {
//        private Set<GrayEventListener<?>> listeners = new LinkedHashSet<>();
//
//
//        public Collection<GrayEventListener<?>> getListeners() {
//            List<GrayEventListener<?>> linstenerList = new ArrayList<>(listeners);
//            AnnotationAwareOrderComparator.sort(linstenerList);
//            return linstenerList;
//        }
//    }


    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }
}
