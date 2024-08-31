package com.jim.Partner_Match.entity.request;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;

@Data
public class UpdateTagRequest implements Serializable {

    private static final long serialVersionUID = -3641351853791810422L;

    private ArrayList<String> tagList;
}
