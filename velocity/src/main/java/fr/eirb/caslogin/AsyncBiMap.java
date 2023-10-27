package fr.eirb.caslogin;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface AsyncBiMap<K, V> extends AsyncMap<K, V>{
	CompletableFuture<Optional<K>> getByValue(V value);
}
