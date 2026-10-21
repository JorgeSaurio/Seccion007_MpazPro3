package com.mpazpro3.app.data.local

import androidx.room.Embedded
import androidx.room.Relation

/**
 * NO es una tabla. Es el resultado de una consulta que junta una actividad con TODAS sus
 * alternativas. Room hace la unión solo gracias a @Relation:
 *   "las alternativas cuyo actividadId sea igual al id de esta actividad".
 */
data class ActividadConAlternativas(
    @Embedded val actividad: ActividadEntity,
    @Relation(
        parentColumn = "id",          // columna de ActividadEntity
        entityColumn = "actividadId"  // columna de AlternativaEntity
    )
    val alternativas: List<AlternativaEntity>
)