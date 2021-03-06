package de.hhu.sharing.data;

import de.hhu.sharing.model.Image;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
@TestPropertySource(locations="classpath:test.properties")
public class ImageRepositoryTest {

    @Autowired
    ImageRepository imageRepo;


    @Test
    public void testFindAll(){
        Image image1 = new Image();
        Image image2 = new Image();

        image1.setMimeType("jpg");
        image2.setMimeType("jpg");

        imageRepo.save(image1);
        imageRepo.save(image2);

        Assertions.assertThat(imageRepo.findAll().size()).isEqualTo(2);
    }
}
