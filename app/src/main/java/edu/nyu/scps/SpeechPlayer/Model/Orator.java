package edu.nyu.scps.SpeechPlayer.Model;

/**
 * Famous US Speeches Android Application
 * Copyright (C) 2015  Asa F. Swain
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * This class is used as a data structure to store the full name and a URL of a portrait image of the orator
 */
public class Orator {

    private String fullName;
    private String portraitURL;

    // copy constructor
    public Orator(Orator other) {
        if (other != null) {
            this.fullName = other.fullName;
            this.portraitURL = other.portraitURL;
        }
    }

    public Orator(String subjectName) {
        this.fullName = subjectName;
    }

    public Orator(String subjectName, String imageURL) {
        this.fullName = subjectName;
        this.portraitURL = imageURL;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPortraitURL() {
        return portraitURL;
    }

    public void setPortraitURL(String portraitURL) {
        this.portraitURL = portraitURL;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Orator portrait = (Orator) o;

        return fullName.equals(portrait.fullName);
    }

    @Override
    public int hashCode() {
        return fullName.hashCode();
    }

    // this method is designed to allow a user to generate a hashcode with just a title and an orator,
    // without having to create a new Speech object
    public static int getHashCode(String subjectName) {
        int hash = 0;
        if (subjectName != null) {
            Orator dummySpeech = new Orator(subjectName);
            hash = dummySpeech.hashCode();
        }
        return hash;
    }
}
