package fr.eirb.caslogin;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface AsyncMap<K,V>{
	CompletableFuture<Void> put(K key, V value);
	CompletableFuture<Void> remove(K key);
	CompletableFuture<Optional<V>> get(K key);
	CompletableFuture<Boolean> contains(K key);
	CompletableFuture<Boolean> containsValue(V value);
}
