package com.tunein.dfpaudiosample.inject.scopes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Scope to restrict usage of components/modules for VideoAd related code.
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface VideoAdScope {
}
