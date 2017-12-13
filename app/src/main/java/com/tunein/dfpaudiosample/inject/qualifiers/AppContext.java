package com.tunein.dfpaudiosample.inject.qualifiers;

import java.lang.annotation.Retention;

import javax.inject.Qualifier;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Qualifier to distinguish Context from Application Context and Activity Context as an example.
 *
 * @see <a href="https://possiblemobile.com/2013/06/context/">What is Context</a>
 */
@Qualifier
@Retention(RUNTIME)
public @interface AppContext {
}
