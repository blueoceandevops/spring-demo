# Run

1. start spring application
2. open [web/html/sse/memory_monitor.html](https://github.com/wu8685/spring-demo/tree/master/web/html/sse) on browser

The memory information will refresh every 3s.

# issue 

## [the unclosed connection will keep reconnecting](https://github.com/wu8685/spring-demo/issues/1)

1. on web side, keep to close EventSource to disconnect sse when unloading
2. on server side, using weak reference to guarantee recycling all sse emitter. The active client event sources will auto reconnect. (api `/sse/gc` can test weak referenced emitter recycle)
