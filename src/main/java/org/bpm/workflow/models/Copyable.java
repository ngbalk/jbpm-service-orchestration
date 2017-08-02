package org.bpm.workflow.models;

/**
 * Created by nbalkiss on 7/13/17.
 */

/**
 * Should copy relevant data from "other" object, if its the same type as the object being copied to.
 * In general, should ignore the primary key 'id' field during copy if this is a persistent entity.
 */
public interface Copyable {

    boolean copy(Object other);
}
