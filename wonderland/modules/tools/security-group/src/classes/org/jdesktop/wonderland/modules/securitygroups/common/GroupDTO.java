/**
 * Project Wonderland
 *
 * Copyright (c) 2004-2010, Sun Microsystems, Inc., All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * Sun designates this particular file as subject to the "Classpath"
 * exception as provided by Sun in the License file that accompanied
 * this code.
 */
package org.jdesktop.wonderland.modules.securitygroups.common;

import java.io.Reader;
import java.io.Writer;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author jkaplan
 */
@XmlRootElement
public class GroupDTO {
    private static JAXBContext jaxbContext;

    /* Create the XML marshaller and unmarshaller once for all ModuleRepositorys */
    static {
        try {
            jaxbContext = JAXBContext.newInstance(GroupDTO.class, MemberDTO.class);
        } catch (javax.xml.bind.JAXBException excp) {
            System.out.println(excp.toString());
        }
    }

    private String id;
    private int memberCount;
    private boolean editable;
    private Set<MemberDTO> members = new TreeSet<MemberDTO>(new MemberComparator());

    public GroupDTO() {
    }

    public GroupDTO(String id) {
        this.id = id;
    }

    @XmlElement
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlElement
    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    @XmlElement
    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    @XmlElement
    public Set<MemberDTO> getMembers() {
        return members;
    }

    public void setMembers(Set<MemberDTO> members) {
        this.members = members;
    }

    /**
     * Takes the input reader of the XML file and instantiates an instance of
     * the GroupDTO class
     * <p>
     * @param r The input stream of the version XML file
     * @throw ClassCastException If the input file does not map to GroupDTO
     * @throw JAXBException Upon error reading the XML file
     */
    public static GroupDTO decode(Reader r) throws JAXBException {
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return (GroupDTO) unmarshaller.unmarshal(r);
    }

    /**
     * Writes the GroupDTO class to an output writer.
     * <p>
     * @param w The output writer to write to
     * @throw JAXBException Upon error writing the XML file
     */
    public void encode(Writer w) throws JAXBException {
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", true);
        marshaller.marshal(this, w);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GroupDTO other = (GroupDTO) obj;
        if ((this.id == null) ? (other.id != null) :
            !this.id.equals(other.id))
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    class MemberComparator implements Comparator<MemberDTO> {
        public int compare(MemberDTO o1, MemberDTO o2) {
            // first compare owners
            if (o1.isOwner() && !o2.isOwner()) {
                return 1;
            } else if (!o1.isOwner() && o2.isOwner()) {
                return -1;
            } else {
                return o1.getId().compareToIgnoreCase(o2.getId());
            }
        }
    }
}
