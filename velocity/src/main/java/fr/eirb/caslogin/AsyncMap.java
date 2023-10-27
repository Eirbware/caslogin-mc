package fr.eirb.caslogin;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface AsyncMap<K,V>{
	CompletableFuture<Set<K>> keys();
	CompletableFuture<Collection<V>> values();
	CompletableFuture<Void> put(K key, V value);
	CompletableFuture<Void> remove(K key);
	CompletableFuture<Optional<V>> get(K key);
	CompletableFuture<Boolean> contains(K key);
	CompletableFuture<Boolean> containsValue(V value);
}
